package io.github.open.easykafka.client.message;

import io.github.open.easykafka.client.model.Tag;
import io.github.open.easykafka.client.model.TopicMetadata;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@Accessors(chain = true)
public abstract class AbstractMessage {

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息主键，@MessageKey 标识的字段值
     */
    private String messageKey;

    /**
     * 消息头，@MessageHeader 标识的字段值
     */
    private Map<String, Object> messageHeader;

    /**
     * 消息打标
     */
    private Tag messageTag;

    /**
     * 用途
     */
    private Usage messageUsage;

    /**
     * 所在服务
     */
    private String messageService;

    /**
     * 主题
     */
    private TopicMetadata messageTopic;

    /**
     * 创建时间
     */
    private Date messageCreateTime;

}
