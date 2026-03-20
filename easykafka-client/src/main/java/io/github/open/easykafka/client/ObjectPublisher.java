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
 * <h3>这是一个 Object 类型的发布器，使用示例：</h3>
 * <pre class="code">
 *     // 定义消息
 *     String message = "Hello!";
 *     // 定义消息元数据
 *     MessageMetadata metadata = new MessageMetadataBuilder()
 *             .topicMetadata("send", "easykafka-sync") // 指定 cluster 和 topic
 *             .messageKey("123")                       // 指定发往 kafka 的 key
 *             .messageTag(Tag.BASE)                    // 指定基线环境
 *             .messageHeader("retryCount", 1)          // 指定消息头
 *             .build();
 *     // 发送消息
 *     ObjectPublisher.publish(message, metadata);
 * </pre>
 *
 * <h3>使用说明：</h3>
 * <li>该发布器支持发送任意类型的消息；</li>
 * <li>在发往 Kafka 前，会先使用 {@link io.github.open.easykafka.client.support.utils.JsonUtils#toJson(Object)} 对该消息进行序列化，如果是 {@link String} 类型的消息，则不会进行序列化；</li>
 * <li>相较而言，建议优先使用 {@link EventPublisher} 发布器；</li>
 *
 * <h3>考虑到兼容性，以下场景只能使用 ObjectPublisher：</h3>
 * <li>消费方消费的是 {@link java.util.List} 类型数据，则只能使用 ObjectPublisher；</li>
 * <li>消费方未监听灰度消息，则只能使用 ObjectPublisher 指定发往基线 （{@link io.github.open.easykafka.client.model.Tag#BASE}）；</li>
 *
 * <@author <a href="https://github.com/studeyang">studeyang</a>
 * @see EventPublisher
 * @since 1.0 2025/5/26
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