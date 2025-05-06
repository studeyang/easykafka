package io.github.open.easykafka.client.producer;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.model.ProducerMetadata;
import io.github.open.easykafka.client.model.Tag;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.properties.EasyKafkaProperties;
import io.github.open.easykafka.client.support.properties.InitProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Slf4j
public class ProducerBeanRegistrar implements BeanDefinitionRegistryPostProcessor {

    private final EasyKafkaProperties easyKafkaProperties;
    private final Map<String, InitProperties.Producer> producerMap;
    private final AtomicInteger counter = new AtomicInteger();

    public ProducerBeanRegistrar(EasyKafkaProperties easyKafkaProperties) {
        this.easyKafkaProperties = easyKafkaProperties;
        this.producerMap = Optional.ofNullable(easyKafkaProperties.getInit())
                .map(InitProperties::getProducer)
                .orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(InitProperties.Producer::getBeanName, Function.identity()));
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        for (InitProperties.KafkaCluster kafkaCluster : easyKafkaProperties.getInit().getKafkaCluster()) {
            Assert.notNull(kafkaCluster.getCluster(),
                    "easykafka.init.kafkaCluster.cluster is null. Please check your config: " + JSON.toJSONString(kafkaCluster));
            if (SpringContext.isGrayEnvironment()) {
                registerGrayProducer(kafkaCluster, registry);
            } else {
                registerBaseProducer(kafkaCluster, registry);
            }
        }
    }


    private void registerBaseProducer(InitProperties.KafkaCluster kafkaCluster, BeanDefinitionRegistry registry) {
        if (kafkaCluster.getTag() == Tag.BASE) {
            registerProducer(kafkaCluster, registry);
        }
    }

    private void registerGrayProducer(InitProperties.KafkaCluster kafkaCluster, BeanDefinitionRegistry registry) {
        if (kafkaCluster.getTag() == Tag.GRAY) {
            registerProducer(kafkaCluster, registry);
        }
    }

    private void registerProducer(InitProperties.KafkaCluster kafkaCluster, BeanDefinitionRegistry registry) {
        // 1. beanName
        String beanName = getBeanName(kafkaCluster);

        // 2. Producer Config
        InitProperties.Producer producerConfig = producerMap.get(beanName);
        Map<String, Object> props = getDefaultProducerConfig(kafkaCluster);
        Optional.ofNullable(producerConfig)
                .map(InitProperties.Producer::getConfig)
                .ifPresent(props::putAll);
        log.info("[EasyKafka] Producer Config: {} --> {}", beanName, JSON.toJSONString(props));

        // 3. Inject Bean
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(StringKafkaProducer.class);
        beanDefinition.setDestroyMethodName("close");
        // 设置构造参数
        ConstructorArgumentValues args = new ConstructorArgumentValues();
        args.addIndexedArgumentValue(0, props);
        args.addIndexedArgumentValue(1, getProducerMetadata(kafkaCluster));
        beanDefinition.setConstructorArgumentValues(args);

        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private String getBeanName(InitProperties.KafkaCluster kafkaCluster) {
        String tag = Tag.GRAY == kafkaCluster.getTag() ? "Gray" : "";
        return kafkaCluster.getCluster() + tag + "Producer";
    }

    private ProducerMetadata getProducerMetadata(InitProperties.KafkaCluster kafkaCluster) {
        ProducerMetadata producerMetadata = new ProducerMetadata(kafkaCluster.getCluster(), kafkaCluster.getTag());
        producerMetadata.setBean(getBeanName(kafkaCluster));
        return producerMetadata;
    }

    private Map<String, Object> getDefaultProducerConfig(InitProperties.KafkaCluster kafkaCluster) {
        Map<String, Object> props = new HashMap<>(4);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster.getBrokers());

        String serviceName = SpringContext.getService();
        if (StringUtils.hasText(serviceName)) {
            props.put(ProducerConfig.CLIENT_ID_CONFIG, serviceName + "-" + counter.getAndIncrement());
        }

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 允许重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // 等待批量发送的延迟: 5ms
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        // 单个消息批次大小: 16k
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16 * 1024);
        // 总缓冲区大小: 32M
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024);

        return props;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 可以在这里对已注册的 Bean 进行后处理
    }

}
