package io.github.open.easykafka.example.handler;

import io.github.open.easykafka.event.Example2Event;
import io.github.open.easykafka.event.ExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
//@EventHandler(cluster = "send", topics = "easykafka-example-topic", groupId = "groupId-example-consumer")
public class BatchEventHandler {

    @KafkaHandler
    public void handle(ExampleEvent event) {
        System.out.println("BatchEventHandler 收到消息: " + event);
    }

    @KafkaHandler
    public void handle(Example2Event event) {
        System.out.println("BatchEventHandler 收到消息: " + event);
    }

}
