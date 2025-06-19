package io.github.open.easykafka.client;

import io.github.open.easykafka.client.message.Event;
import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.model.MessageMetadataBuilder;
import io.github.open.easykafka.client.producer.MessagePublisher;
import io.github.open.easykafka.client.support.MessageIntrospector;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 事件发布器
 * @author studeyang
 */
@UtilityClass
@Slf4j
public final class EventPublisher {

    @Setter
    private static MessagePublisher publisher;

    /**
     * 发布一个事件
     */
    public static void publish(Event event) {
        try {
            MessageMetadata metadata = new MessageMetadataBuilder()
                    .topicMetadata(MessageIntrospector.getTopic(event.getClass()))
                    .messageKey(MessageIntrospector.getMessageKey(event))
                    .messageHeaders(MessageIntrospector.getMessageHeaders(event))
                    .build();
            publisher.publish(event, metadata);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发布一批事件
     */
    public static void publish(Collection<? extends Event> events) {
        publisher.publish(events);
    }

}