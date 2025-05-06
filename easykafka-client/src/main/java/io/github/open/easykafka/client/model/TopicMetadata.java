package io.github.open.easykafka.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class TopicMetadata {

    private String cluster;
    private String name;

}
