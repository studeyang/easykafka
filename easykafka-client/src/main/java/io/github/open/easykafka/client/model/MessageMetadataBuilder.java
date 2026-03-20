package io.github.open.easykafka.client.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用示例：
 * <pre class="code">
 * MessageMetadata metadata = new MessageMetadataBuilder()
 * 			.topicMetadata("send", "easykafka-sync") // 指定 cluster 和 topic
 * 			.messageKey("123")                       // 指定发往 kafka 的 key
 * 			.messageTag(Tag.BASE)                    // 指定基线环境
 * 			.messageHeader("retryCount", 1)          // 指定消息头
 * 			.build();
 * </pre>
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @see io.github.open.easykafka.client.ObjectPublisher
 * @since 1.0 2025/5/26
 */
public class MessageMetadataBuilder {

    private final MessageMetadata metadata;

    public MessageMetadataBuilder() {
        metadata = new MessageMetadata();
    }

    /**
     * 指定消息要发往哪个 topic
     */
    public MessageMetadataBuilder topicMetadata(TopicMetadata topicMetadata) {
        metadata.setTopicMetadata(topicMetadata);
        return this;
    }

    /**
     * 指定消息要发往哪个 cluster, topic
     */
    public MessageMetadataBuilder topicMetadata(String cluster, String topic) {
        metadata.setTopicMetadata(new TopicMetadata(cluster, topic));
        return this;
    }

    /**
     * 指定消息 key (相同的key会发到kafka的同一个分区)
     */
    public MessageMetadataBuilder messageKey(String messageKey) {
        metadata.setMessageKey(messageKey);
        return this;
    }

    /**
     * 指定消息要发往的环境 (生产基线/生产灰度)
     */
    public MessageMetadataBuilder messageTag(Tag tag) {
        metadata.setMessageTag(tag);
        return this;
    }

    /**
     * 指定消息 header (该方法可多次调用, 表示多个 header)
     * <p><i>
     * 只支持 {@link String}, {@link Integer} 类型的消息头
     * （详见 {@link io.github.open.easykafka.client.support.MessageIntrospector#checkMessageHeaderType(Object)}）
     * </i></p>
     */
    public MessageMetadataBuilder messageHeader(String key, Object value) {
        if (metadata.getMessageHeaders() == null) {
            metadata.setMessageHeaders(new HashMap<>(4));
        }
        metadata.getMessageHeaders().put(key, value);
        return this;
    }

    /**
     * 指定消息 header 集合
     */
    public MessageMetadataBuilder messageHeaders(Map<String, Object> messageHeaders) {
        if (metadata.getMessageHeaders() == null) {
            metadata.setMessageHeaders(new HashMap<>(4));
        }
        metadata.getMessageHeaders().putAll(messageHeaders);
        return this;
    }

    public MessageMetadata build() {
        return metadata;
    }

}
