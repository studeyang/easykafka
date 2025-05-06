package io.github.open.easykafka.client.message;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.open.easykafka.client.model.TopicMetadata;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

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
    private String id;

    /**
     * 用途
     */
    private Usage usage;

    /**
     * 所在服务
     */
    private String service;

    /**
     * 主题
     */
    @JSONField(serialize = false)
    private TopicMetadata topic;

    /**
     * 创建时间
     */
    private Date timeStamp;

}
