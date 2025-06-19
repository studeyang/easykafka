package io.github.open.easykafka.example.configuration;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.support.converter.FastJsonMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
public class ConsumerConfiguration {

    @Value("${easykafka.init.kafkaCluster[0].brokers}")
    private String brokers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AbstractMessage> sendKafkaListenerContainerFactory(
            KafkaListenerEndpointRegistry endpointRegistry) {
        ConcurrentKafkaListenerContainerFactory<String, AbstractMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setMessageConverter(new FastJsonMessageConverter());
        factory.setConcurrency(4);
        factory.getContainerProperties().setPollTimeout(1500);
        factory.setBatchListener(true);

        ConsumerFactory<String, AbstractMessage> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs(brokers));
        factory.setConsumerFactory(consumerFactory);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        factory.setContainerCustomizer(container -> {
            /* customize the container */
            log.info("metrics: " + JSON.toJSONString(container.metrics()));
            container.setupMessageListener("");
        });

        return factory;
    }

    public Map<String, Object> consumerConfigs(String servers) {
        Map<String, Object> propsMap = new HashMap<>(8);
        propsMap.put("bootstrap.servers", servers);
        propsMap.put("enable.auto.commit", true);
        propsMap.put("auto.commit.interval.ms", "100");
        propsMap.put("session.timeout.ms", "6000");
        propsMap.put("key.deserializer", StringDeserializer.class);
        propsMap.put("value.deserializer", StringDeserializer.class);
        propsMap.put("auto.offset.reset", "latest");
        return propsMap;
    }

}