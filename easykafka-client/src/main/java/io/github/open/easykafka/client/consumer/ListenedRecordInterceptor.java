package io.github.open.easykafka.client.consumer;

import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.TopicMetadata;
import io.github.open.easykafka.client.support.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.KafkaUtils;

import java.util.Optional;

/**
 * 过滤掉未监听的 Event
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/23
 */
@Slf4j
public class ListenedRecordInterceptor implements RecordInterceptor<String, AbstractMessage> {

    @Override
    public ConsumerRecord<String, AbstractMessage> intercept(ConsumerRecord<String, AbstractMessage> consumerRecord) {
        AbstractMessage message = consumerRecord.value();
        if (message == null) {
            return null;
        }
        if (!ListenerContainer.isListenOn(message)) {
            TopicMetadata topicMetadata = Optional.of(message)
                    .map(AbstractMessage::getMessageTopic)
                    .orElse(new TopicMetadata());
            log.warn("Event Not Found In {}! GroupId={} Cluster={} Topic={}", message.getClass().getName(),
                    KafkaUtils.getConsumerGroupId(), topicMetadata.getCluster(), topicMetadata.getName());
            return null;
        }
        String traceId = getHeaderValue(consumerRecord.headers(), "Pinpoint-TraceID");
        String spanId = getHeaderValue(consumerRecord.headers(), "Pinpoint-SpanID");
        // 提交位移时, 提交的是 offset+1
        log.info("[traceId={}, spanId={}] Received A Message Offset={} Content={}", traceId, spanId, consumerRecord.offset(), JsonUtils.toJson(consumerRecord.value()));
        return consumerRecord;
    }

    private String getHeaderValue(Headers headers, String headerKey) {
        return Optional.ofNullable(headers)
                .map(hs -> hs.lastHeader(headerKey))
                .map(Header::value)
                .map(String::new)
                .orElse("");
    }

}
