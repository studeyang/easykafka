package io.github.open.easykafka.event;

import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.message.Event;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@ToString(callSuper = true)
@Topic(cluster = "send", name = "easykafka-example-topic")
public class Example2Event extends Event {

    private String name;

}
