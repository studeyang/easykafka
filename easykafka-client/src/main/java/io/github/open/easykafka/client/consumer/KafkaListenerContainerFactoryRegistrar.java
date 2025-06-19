package io.github.open.easykafka.client.consumer;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.MessageConstant;
import io.github.open.easykafka.client.model.Tag;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.converter.FastJsonMessageConverter;
import io.github.open.easykafka.client.support.properties.EasyKafkaProperties;
import io.github.open.easykafka.client.support.properties.InitProperties;
import io.github.open.easykafka.client.support.serializer.MessageDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应在这之前执行: {@link KafkaListenerAnnotationBeanPostProcessor#postProcessAfterInitialization(Object, String)}
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Slf4j
public class KafkaListenerContainerFactoryRegistrar implements BeanPostProcessor {

    private final EasyKafkaProperties easyKafkaProperties;
    private final Map<String, InitProperties.Consumer> consumerMap;

    public KafkaListenerContainerFactoryRegistrar(EasyKafkaProperties easyKafkaProperties) {
        this.easyKafkaProperties = easyKafkaProperties;
        this.consumerMap = Optional.ofNullable(easyKafkaProperties.getInit())
                .map(InitProperties::getConsumer)
                .orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(InitProperties.Consumer::getBeanName, Function.identity()));
    }

    @PostConstruct
    public void initListenerContainerFactory() {
        for (InitProperties.KafkaCluster kafkaCluster : easyKafkaProperties.getInit().getKafkaCluster()) {
            Assert.notNull(kafkaCluster.getCluster(),
                    "easykafka.init.kafkaCluster.cluster is null. Please check your config: " + JSON.toJSONString(kafkaCluster));
            if (SpringContext.isGrayEnvironment()) {
                registerGray(kafkaCluster);
            } else {
                registerBase(kafkaCluster);
            }
        }
    }

    private void registerBase(InitProperties.KafkaCluster kafkaCluster) {
        if (kafkaCluster.getTag() == Tag.BASE) {
            registerListenerContainerFactory(kafkaCluster);
        }
    }

    private void registerGray(InitProperties.KafkaCluster kafkaCluster) {
        if (kafkaCluster.getTag() == Tag.GRAY) {
            registerListenerContainerFactory(kafkaCluster);
        }
    }

    private void registerListenerContainerFactory(InitProperties.KafkaCluster kafkaCluster) {

        // 1. beanName
        String beanName = getContainerFactoryBeanName(kafkaCluster);

        // 2. Consumer Config
        String consumerBeanName = getConsumerBeanName(kafkaCluster);
        InitProperties.Consumer consumerConfig = consumerMap.get(consumerBeanName);
        Map<String, Object> props = getDefaultConsumerConfig(kafkaCluster);
        Optional.ofNullable(consumerConfig)
                .map(InitProperties.Consumer::getConfig)
                .ifPresent(props::putAll);
        log.info("[EasyKafka] Consumer Config: {} --> {}", consumerBeanName, JSON.toJSONString(props));

        // 3. Inject Bean
        ConcurrentKafkaListenerContainerFactory<String, AbstractMessage> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();

        containerFactory.setMessageConverter(new FastJsonMessageConverter());
        containerFactory.setRecordInterceptor(new ListenedRecordInterceptor());
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        ConsumerFactory<String, AbstractMessage> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        containerFactory.setConsumerFactory(consumerFactory);

        SpringContext.register(beanName, containerFactory);
    }

    private String getContainerFactoryBeanName(InitProperties.KafkaCluster kafkaCluster) {
        return kafkaCluster.getCluster() + MessageConstant.KAFKA_LISTENER_CONTAINER_FACTORY;
    }

    private String getConsumerBeanName(InitProperties.KafkaCluster kafkaCluster) {
        String tag = Tag.GRAY == kafkaCluster.getTag() ? "Gray" : "";
        return kafkaCluster.getCluster() + tag + "Consumer";
    }

    private Map<String, Object> getDefaultConsumerConfig(InitProperties.KafkaCluster kafkaCluster) {
        Map<String, Object> props = new HashMap<>(8);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster.getBrokers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);

        return props;
    }

}
