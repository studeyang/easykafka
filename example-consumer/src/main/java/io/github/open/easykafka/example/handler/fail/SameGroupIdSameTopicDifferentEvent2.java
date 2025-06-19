package io.github.open.easykafka.example.handler.fail;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.message.IEventHandler;
import io.github.open.easykafka.event.Example2Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
@EventHandler(cluster = "send", topics = "easykafka-example-topic")
public class SameGroupIdSameTopicDifferentEvent2 implements IEventHandler {

    @KafkaHandler
    public void handle(Example2Event event) {
        log.info("收到消息 {}", event);
    }

}
