package io.github.open.easykafka.example.handler;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.event.Example2Event;
import io.github.open.easykafka.event.ExampleEvent;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.stereotype.Service;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Service
public class SingleEventHandler {

    private static final String ERROR = "error";

    @EventHandler(properties = {"enable.auto.commit=false", "max.poll.records=50"})
    public void handle(ExampleEvent event, ConsumerRecordMetadata metadata) {
        System.out.printf("SingleEventHandler 收到消息 offset: %s, message: %s%n", metadata.offset(), event);
        if (event.getName().contains(ERROR)) {
            throw new RuntimeException("test retry");
        }
    }

    @EventHandler(cluster = "send", topics = "easykafka-example-topic", groupId = "groupId-example-consumer")
    public void handle(Example2Event event) {
        System.out.println("SingleEventHandler 收到消息: " + event);
    }

}
