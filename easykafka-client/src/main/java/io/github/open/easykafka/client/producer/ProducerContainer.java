package io.github.open.easykafka.client.producer;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.ProducerMetadata;
import io.github.open.easykafka.client.model.Tag;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.model.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Slf4j
public class ProducerContainer implements InitializingBean {

    private final Map<ProducerMetadata, StringKafkaProducer> producerMap;

    public ProducerContainer(List<StringKafkaProducer> producerList) {
        this.producerMap = producerList.stream()
                .collect(Collectors.toMap(StringKafkaProducer::getProducerMetadata, Function.identity()));
    }

    public StringKafkaProducer getProducer(String cluster) {
        Tag tag = SpringContext.isGrayEnvironment() ? Tag.GRAY : Tag.BASE;

        ProducerMetadata producerMetadata = new ProducerMetadata(cluster, tag);
        StringKafkaProducer kafkaProducer = producerMap.get(producerMetadata);

        if (kafkaProducer == null) {
            throw new ProducerException(ErrorCode.PRODUCER_NOT_FOUND);
        }
        return kafkaProducer;
    }

    @Override
    public void afterPropertiesSet() {
        String result = "[EasyKafka] ProducerContainer:\n" +
                producerMap.keySet().stream()
                        .sorted(Comparator.comparing(ProducerMetadata::getTag))
                        .map(JSON::toJSONString)
                        .collect(Collectors.joining("\n"));
        log.info(result);
    }
}
