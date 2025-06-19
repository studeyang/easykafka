package io.github.open.easykafka.example.handler;

import io.github.open.easykafka.client.message.IEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;

import java.nio.ByteBuffer;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Slf4j
public class MixListenerHandler implements IEventHandler {

    @KafkaListener(id = "Send-Post.easykafka-example-topic", topics = "easykafka-example-topic")
    public void handle1(ConsumerRecord<String, String> record,
                        @Header("retryCount") ByteBuffer retryCount) {
        org.apache.kafka.common.header.Header retryCountHeader = record.headers().lastHeader("retryCount");
        org.apache.kafka.common.header.Header nameHeader = record.headers().lastHeader("name");
        log.info("retryCount: {}", retryCount.getInt());
        log.info("收到消息 message: {}, retryCount: {}, name: {}",
                record.value(),
                ByteBuffer.wrap(retryCountHeader.value()).getInt(),
                new String(nameHeader.value()));
    }

    @KafkaListener(id = "group-example-consumer", topics = "easykafka-sync")
    public void handle(ConsumerRecord<String, String> record) {

        byte[] bytes = record.headers().lastHeader("retryCount").value();
        Integer retryCount = ByteBuffer.wrap(bytes).getInt();

        log.info("收到消息, retryCount: {}, key: {}, value: {}", retryCount, record.key(), record.value());
    }

}
