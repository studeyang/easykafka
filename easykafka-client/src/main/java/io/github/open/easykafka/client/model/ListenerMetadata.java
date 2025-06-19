package io.github.open.easykafka.client.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@EqualsAndHashCode(of = {"groupId", "topics", "cluster", "event"})
public class ListenerMetadata {

    @JSONField(ordinal = 1)
    private String groupId;

    @JSONField(serialize = false)
    private String cluster;

    @JSONField(ordinal = 2)
    private String topics;

    @JSONField(ordinal = 3)
    private String event;

}
