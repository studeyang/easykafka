package io.github.open.easykafka.example.event;

import io.github.open.easykafka.client.message.Event;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/17
 */
@Data
public class NoTopicAnnotationEvent extends Event {

    private String name;

}
