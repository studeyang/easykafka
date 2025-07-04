package io.github.open.easykafka.client.producer;

import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
public class StringKafkaProducer extends KafkaProducer<String, String> {

    @Getter
    private final String beanName;

    public StringKafkaProducer(Map<String, Object> configs, String beanName) {
        super(configs);
        this.beanName = beanName;
    }

}
