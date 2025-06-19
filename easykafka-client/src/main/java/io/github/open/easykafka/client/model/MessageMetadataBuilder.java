package io.github.open.easykafka.client.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/26
 */
public class MessageMetadataBuilder {

    private final MessageMetadata metadata;

    public MessageMetadataBuilder() {
        metadata = new MessageMetadata();
    }

    public MessageMetadataBuilder topicMetadata(TopicMetadata topicMetadata) {
        metadata.setTopicMetadata(topicMetadata);
        return this;
    }

    public MessageMetadataBuilder topicMetadata(String cluster, String topic) {
        metadata.setTopicMetadata(new TopicMetadata(cluster, topic));
        return this;
    }

    public MessageMetadataBuilder messageKey(String messageKey) {
        metadata.setMessageKey(messageKey);
        return this;
    }

    public MessageMetadataBuilder messageTag(Tag tag) {
        metadata.setMessageTag(tag);
        return this;
    }

    public MessageMetadataBuilder messageHeader(String key, Object value) {
        if (metadata.getMessageHeaders() == null) {
            metadata.setMessageHeaders(new HashMap<>(4));
        }
        metadata.getMessageHeaders().put(key, value);
        return this;
    }

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
