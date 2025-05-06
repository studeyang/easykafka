package io.github.open.easykafka.client.support.properties;

import io.github.open.easykafka.client.model.Tag;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author studeyang
 */
@Data
public class InitProperties {
    private List<KafkaCluster> kafkaCluster;
    private List<Producer> producer;
    private List<Consumer> consumer;


    @Data
    public static class KafkaCluster {
        private String cluster;
        private String brokers;
        private Tag tag = Tag.BASE;
    }

    @Data
    public static class Producer {
        private String beanName;
        private Map<String, String> config;
    }

    @Data
    public static class Consumer {
        private String beanName;
        private String cluster;
        private Map<String, String> config;
    }

}