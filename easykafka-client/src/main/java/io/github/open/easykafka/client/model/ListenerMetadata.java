package io.github.open.easykafka.client.model;

import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
public class ListenerMetadata {

    private String groupId;
    private String topics;
    private String cluster;
    private String event;

}
