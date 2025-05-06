package io.github.open.easykafka.client.support.converter;

import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.support.utils.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.messaging.Message;

import java.lang.reflect.Type;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/22
 */
public class FastJsonMessageConverter extends MessagingMessageConverter {

    @Override
    protected Object convertPayload(Message<?> message) {
        throw new UnsupportedOperationException("Select a subclass that creates a ProducerRecord value "
                + "corresponding to the configured Kafka Serializer");
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> consumerRecord, Type type) {
        Object value = consumerRecord.value();
        if (consumerRecord.value() == null) {
            return KafkaNull.INSTANCE;
        }

        if (value instanceof String) {
            try {
                return JsonUtils.parse((String) value);
            } catch (Exception e) {
                throw new ConversionException("Failed to convert from JSON", e);
            }
        } else if (value instanceof AbstractMessage) {
            return value;
        } else {
            throw new IllegalStateException("Only String supported");
        }
    }

}
