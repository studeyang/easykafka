package io.github.open.easykafka.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@EqualsAndHashCode(of = {"cluster", "tag"})
public class ProducerMetadata {

    private String bean;
    private String cluster;
    private Tag tag;

    public ProducerMetadata(String cluster, Tag tag) {
        this.cluster = cluster;
        this.tag = tag;
    }

}
