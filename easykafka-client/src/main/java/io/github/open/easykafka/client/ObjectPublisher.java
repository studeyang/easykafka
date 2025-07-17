package io.github.open.easykafka.client;

import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.model.MessageMetadataBuilder;
import io.github.open.easykafka.client.model.TopicMetadata;
import io.github.open.easykafka.client.producer.MessagePublisher;
import io.github.open.easykafka.client.producer.callback.DefaultMessageCallback;
import io.github.open.easykafka.client.producer.callback.MessageCallback;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * Object消息发布器
 *
 * @author 005964
 */
@UtilityClass
public final class ObjectPublisher {

    @Setter
    private static MessagePublisher publisher;

    /**
     * 发布一个Object消息
     */
    public static void publish(Object message, TopicMetadata topic) {
        publish(message, new MessageMetadataBuilder().topicMetadata(topic).build(), new DefaultMessageCallback());
    }

    /**
     * 发布一个Object消息
     */
    public static void publish(Object message, TopicMetadata topic, MessageCallback messageCallback) {
        publish(message, new MessageMetadataBuilder().topicMetadata(topic).build(), messageCallback);
    }

    /**
     * 发布一个Object消息
     */
    public static void publish(Object message, MessageMetadata metadata) {
        publisher.publish(message, metadata, new DefaultMessageCallback());
    }

    /**
     * 发布一个Object消息
     */
    public static void publish(Object message, MessageMetadata metadata, MessageCallback messageCallback) {
        publisher.publish(message, metadata, messageCallback);
    }

}