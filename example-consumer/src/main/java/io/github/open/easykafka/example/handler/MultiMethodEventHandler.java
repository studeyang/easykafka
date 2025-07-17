package io.github.open.easykafka.example.handler;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.message.IEventHandler;
import io.github.open.easykafka.event.Example2Event;
import io.github.open.easykafka.event.ExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.messaging.handler.annotation.Header;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
@EventHandler(cluster = "send", topics = "easykafka-example-topic", concurrency = "3")
public class MultiMethodEventHandler implements IEventHandler {

    @KafkaHandler
    public void handle(ExampleEvent event, @Header(value = "retryCount", required = false) ByteBuffer retryCount) {
        log.info("收到消息 retryCount: {}, message: {}", Optional.ofNullable(retryCount).map(ByteBuffer::getInt).orElse(0), event);
    }

    @KafkaHandler
    public void handle(Example2Event event) {
        log.info("收到消息 message: {}", event);
    }

}
