package io.github.open.easykafka.client.producer;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

import static io.github.open.easykafka.client.model.ErrorCode.PRODUCER_NOT_FOUND;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Slf4j
public class ProducerContainer implements InitializingBean {

    private final Map<String, StringKafkaProducer> producerMap;

    public ProducerContainer(Map<String, StringKafkaProducer> producerMap) {
        this.producerMap = producerMap;
    }

    public StringKafkaProducer getProducer(String cluster, Tag tag) {
        if (Tag.BASE == tag) {
            return getProducerByBeanName(cluster + "BaseProducer");
        } else if (Tag.GRAY == tag) {
            return getProducerByBeanName(cluster + "GrayProducer");
        } else {
            return getProducerByBeanName(cluster + "Producer");
        }
    }

    private StringKafkaProducer getProducerByBeanName(String beanName) {
        StringKafkaProducer kafkaProducer = producerMap.get(beanName);
        if (kafkaProducer == null) {
            throw new ProducerException(PRODUCER_NOT_FOUND);
        }
        return kafkaProducer;
    }

    @Override
    public void afterPropertiesSet() {
        String result = "[EasyKafka] ProducerContainer:\n" + producerMap.keySet();
        log.info(result);
    }
}
