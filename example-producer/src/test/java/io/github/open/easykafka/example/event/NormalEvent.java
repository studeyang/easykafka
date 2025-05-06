package io.github.open.easykafka.example.event;

import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.message.Event;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/17
 */
@Data
@Topic(cluster = "send", name = "easykafka-example-topic")
public class NormalEvent extends Event {

    private String name;

}
