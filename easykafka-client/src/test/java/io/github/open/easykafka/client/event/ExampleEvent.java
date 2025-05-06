package io.github.open.easykafka.client.event;

import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.message.Event;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Data
@Topic(cluster = "send", name = "easykafka-example-topic")
public class ExampleEvent extends Event {

    private String name;
    private String address;
    private Sub sub;

    @Data
    public static class Sub {
        private String subName;
    }

}
