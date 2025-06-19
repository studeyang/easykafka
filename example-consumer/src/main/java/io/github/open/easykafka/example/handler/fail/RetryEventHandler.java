package io.github.open.easykafka.example.handler.fail;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.event.ExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
@EventHandler(cluster = "send", topics = "easykafka-example-topic")
public class RetryEventHandler {

    private static final String ERROR = "error";

    @KafkaHandler
    public void handle(ExampleEvent event, ConsumerRecordMetadata metadata) {
        log.info("收到消息 offset: {}, message: {}", metadata.offset(), event);
        if (event.getName().contains(ERROR)) {
            throw new RuntimeException("test retry");
        }
    }

}
