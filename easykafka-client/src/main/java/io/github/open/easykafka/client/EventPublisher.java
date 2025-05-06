package io.github.open.easykafka.client;

import io.github.open.easykafka.client.message.Event;
import io.github.open.easykafka.client.producer.MessagePublisher;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * 事件发布器
 * @author studeyang
 */
@UtilityClass
public final class EventPublisher {

    @Setter
    private static MessagePublisher publisher;

    /**
     * 发布一个事件
     */
    public static void publish(Event event) {
        publisher.publish(event);
    }

    /**
     * 发布一批事件
     */
    public static void publish(Collection<? extends Event> events) {
        publisher.publish(events);
    }

}