package io.github.open.easykafka.example.configuration;

import io.github.open.easykafka.client.support.properties.EasyKafkaProperties;
import io.github.open.easykafka.client.support.properties.InitProperties;
import io.github.open.easykafka.example.handler.*;
import io.github.open.easykafka.example.handler.fail.SameGroupIdSameTopicDifferentEvent1;
import io.github.open.easykafka.example.handler.fail.SameGroupIdSameTopicDifferentEvent2;
import io.github.open.easykafka.example.handler.fail.SameGroupIdSameTopicSameEvent1;
import io.github.open.easykafka.example.handler.fail.SameGroupIdSameTopicSameEvent2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/12
 */
@Configuration
public class SystemConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(EasyKafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();

        Map<String, Object> props = getDefaultConsumerConfig(kafkaProperties.getInit().getKafkaCluster().get(0));
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        containerFactory.setConsumerFactory(consumerFactory);

        return containerFactory;
    }

    private Map<String, Object> getDefaultConsumerConfig(InitProperties.KafkaCluster kafkaCluster) {
        Map<String, Object> props = new HashMap<>(8);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster.getBrokers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return props;
    }

    /////////////// error case ///////////////

    @Configuration
    @ConditionalOnProperty(name = "error.same-group-id.same-topic.same-event", havingValue = "true")
    static class SameGroupIdSameTopicSameEvent {
        @Bean
        public SameGroupIdSameTopicSameEvent1 sameGroupIdSameTopicSameEvent1() {
            return new SameGroupIdSameTopicSameEvent1();
        }
        @Bean
        public SameGroupIdSameTopicSameEvent2 sameGroupIdSameTopicSameEvent2() {
            return new SameGroupIdSameTopicSameEvent2();
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "error.same-group-id.same-topic.different-event", havingValue = "true")
    static class SameGroupIdSameTopicDifferentEvent {
        @Bean
        public SameGroupIdSameTopicDifferentEvent1 sameGroupIdSameTopicDifferentEvent1() {
            return new SameGroupIdSameTopicDifferentEvent1();
        }
        @Bean
        public SameGroupIdSameTopicDifferentEvent2 sameGroupIdSameTopicDifferentEvent2() {
            return new SameGroupIdSameTopicDifferentEvent2();
        }
    }



    /////////////// normal case ///////////////

    @ConditionalOnProperty(name = "normal.different-group-id.same-topic", havingValue = "true")
    static class DifferentGroupIdSameTopicHandler {
        @Bean
        public DifferentGroupIdSameTopicHandler1 differentGroupIdSameTopicHandler1() {
            return new DifferentGroupIdSameTopicHandler1();
        }
        @Bean
        public DifferentGroupIdSameTopicHandler2 differentGroupIdSameTopicHandler2() {
            return new DifferentGroupIdSameTopicHandler2();
        }
    }

    @ConditionalOnProperty(name = "normal.same-group-id.different-topic", havingValue = "true")
    static class SameGroupIdDifferentTopicHandler {
        @Bean
        public SameGroupIdDifferentTopicHandler1 sameGroupIdDifferentTopicHandler1() {
            return new SameGroupIdDifferentTopicHandler1();
        }
        @Bean
        public SameGroupIdDifferentTopicHandler2 sameGroupIdDifferentTopicHandler2() {
            return new SameGroupIdDifferentTopicHandler2();
        }
    }

    @Bean
    @ConditionalOnProperty(name = "normal.multi-method", havingValue = "true")
    public MultiMethodEventHandler multiMethodEventHandler() {
        return new MultiMethodEventHandler();
    }

    @Configuration
    @ConditionalOnProperty(name = "normal.mix-listener", havingValue = "true")
    static class MixListenerConfiguration {
        @Bean
        public MixListenerHandler mixListenerHandler() {
            return new MixListenerHandler();
        }

        @Bean
        public MultiMethodEventHandler multiMethodEventHandler() {
            return new MultiMethodEventHandler();
        }

        @Bean
        public MultiMethodEventHandler2 multiMethodEventHandler2() {
            return new MultiMethodEventHandler2();
        }

    }

}
