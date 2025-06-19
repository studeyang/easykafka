package io.github.open.easykafka.example.producer;

import io.github.open.easykafka.client.ObjectPublisher;
import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.model.MessageMetadataBuilder;
import io.github.open.easykafka.client.model.Tag;
import io.github.open.easykafka.client.model.TopicMetadata;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 这里是异步执行
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ObjectPublisherTest {

    @After
    @SneakyThrows
    public void after() {
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void topicMetadata() {
        List<String> message = new ArrayList<>();
        message.add("test");
        message.add("message");

        ObjectPublisher.publish(message, new TopicMetadata("send", "easykafka-sync"));
    }

    @Test
    public void messageMetadata() {
        List<String> message = new ArrayList<>();
        message.add("test");
        message.add("message");

        MessageMetadata metadata = new MessageMetadataBuilder()
                .topicMetadata("send", "easykafka-sync")
                .messageKey("123")
                .messageTag(Tag.BASE)
                .messageHeader("retryCount", 1)
                .build();

        ObjectPublisher.publish(message, metadata);
    }

}
