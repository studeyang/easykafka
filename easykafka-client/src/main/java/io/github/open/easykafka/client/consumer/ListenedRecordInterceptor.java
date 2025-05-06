package io.github.open.easykafka.client.consumer;

import io.github.open.easykafka.client.message.AbstractMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;

/**
 * 过滤掉未监听的 Event
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/23
 */
public class ListenedRecordInterceptor implements RecordInterceptor<String, AbstractMessage> {

    @Override
    public ConsumerRecord<String, AbstractMessage> intercept(ConsumerRecord<String, AbstractMessage> consumerRecord) {
        if (consumerRecord.value() == null) {
            return null;
        }
        return ListenerContainer.isListenOn(consumerRecord.value()) ? consumerRecord : null;
    }

}
