package io.github.open.easykafka.boot;

import com.alibaba.ttl.threadpool.TtlExecutors;
import io.github.open.easykafka.client.EventPublisher;
import io.github.open.easykafka.client.ObjectPublisher;
import io.github.open.easykafka.client.consumer.EventHandlerAnnotationBeanPostProcessor;
import io.github.open.easykafka.client.consumer.KafkaListenerContainerFactoryRegistrar;
import io.github.open.easykafka.client.consumer.ListenerContainer;
import io.github.open.easykafka.client.model.MessageConstant;
import io.github.open.easykafka.client.producer.*;
import io.github.open.easykafka.client.producer.sender.ISender;
import io.github.open.easykafka.client.producer.sender.ProducerSender;
import io.github.open.easykafka.client.producer.sender.ReportableSender;
import io.github.open.easykafka.client.producer.sender.RetryableSender;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.properties.EasyKafkaProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Configuration
@EnableRetry
@EnableKafka
public class EasyKafkaAutoConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    public EasyKafkaProperties easyKafkaProperties(Environment environment) {
        EasyKafkaProperties properties = new EasyKafkaProperties();
        Binder binder = Binder.get(environment);
        binder.bind("easykafka", Bindable.ofInstance(properties));
        return properties;
    }

    @Configuration
    static class ProducerAutoConfiguration {

        @Bean
        public ExecutorService producerExecutor(EasyKafkaProperties properties) {
            ExecutorService executorService = properties.getRuntime().getProducer().getAsync().create();
            return TtlExecutors.getTtlExecutorService(executorService);
        }

        @Bean
        @DependsOn(MessageConstant.SPRING_CONTEXT_BEAN)
        public ProducerBeanRegistrar producerBeanRegistrar(EasyKafkaProperties easyKafkaProperties) {
            return new ProducerBeanRegistrar(easyKafkaProperties);
        }

        @Bean
        public ProducerContainer producerContainer(Map<String, StringKafkaProducer> producerMap) {
            return new ProducerContainer(producerMap);
        }

        @Bean
        public ISender reportableSender(ProducerContainer producerContainer,
                                        EasyKafkaProperties easyKafkaProperties) {
            int partitionSize = easyKafkaProperties.getRuntime().getProducer().getPartitionSize();
            ProducerSender producerSender = new ProducerSender(producerContainer, partitionSize);

            RetryableSender retryableSender = new RetryableSender(producerSender);

            return new ReportableSender(retryableSender);
        }

        @Bean
        public MessagePublisher defaultMessagePublisher(ISender reportableSender,
                                                        ExecutorService producerExecutor) {
            MessagePublisher messagePublisher = new DefaultMessagePublisher(reportableSender, producerExecutor);
            setPublisher(messagePublisher);
            return messagePublisher;
        }

        private void setPublisher(MessagePublisher defaultMessagePublisher) {
            EventPublisher.setPublisher(defaultMessagePublisher);
            ObjectPublisher.setPublisher(defaultMessagePublisher);
        }
    }

    @Configuration
    static class ConsumerAutoConfiguration {

        @Bean
        public KafkaListenerContainerFactoryRegistrar kafkaListenerContainerFactoryRegistrar(EasyKafkaProperties easyKafkaProperties) {
            return new KafkaListenerContainerFactoryRegistrar(easyKafkaProperties);
        }

        @Bean
        public ListenerContainer listenerContainer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
            return new ListenerContainer(kafkaListenerEndpointRegistry);
        }

        @Bean
        public EventHandlerAnnotationBeanPostProcessor eventHandlerAnnotationBeanPostProcessor(ListenerContainer listenerContainer) {
            return new EventHandlerAnnotationBeanPostProcessor(listenerContainer);
        }

    }

}
