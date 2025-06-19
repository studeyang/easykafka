package io.github.open.easykafka.client.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * See: {@link MessageMetadataBuilder}
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/26
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageMetadata {

    private TopicMetadata topicMetadata;

    /**
     * example: metadata.setMessageKey(req.getOrderId)
     */
    private String messageKey;

    /**
     * 消息打标后将发往该标签环境
     */
    private Tag messageTag;

    /**
     * example: retryCount -> 1
     */
    private Map<String, Object> messageHeaders;

}
