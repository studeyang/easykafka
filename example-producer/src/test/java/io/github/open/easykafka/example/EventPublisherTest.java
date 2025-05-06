package io.github.open.easykafka.example;

import io.github.open.easykafka.event.ExampleEvent;
import io.github.open.easykafka.client.EventPublisher;
import io.github.open.easykafka.example.event.BlankClusterEvent;
import io.github.open.easykafka.example.event.NoTopicAnnotationEvent;
import io.github.open.easykafka.example.event.NormalEvent;
import io.github.open.easykafka.example.event.NotSuchClusterEvent;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * 这里是异步执行
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPublisherTest {

    @After
    @SneakyThrows
    public void after() {
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void publishError() {
        NoTopicAnnotationEvent event = new NoTopicAnnotationEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }

    @Test
    public void validateError() {
        BlankClusterEvent event = new BlankClusterEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }

    @Test
    public void retry() {
        NotSuchClusterEvent event = new NotSuchClusterEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }

    @Test
    public void retryBatch() {
        NotSuchClusterEvent event = new NotSuchClusterEvent();
        event.setName("test");
        EventPublisher.publish(Lists.newArrayList(event));
    }

    @Test
    public void sendNormal() {
        NormalEvent event = new NormalEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }

    @Test
    public void sendExampleEvent() {
        ExampleEvent event = new ExampleEvent();
        event.setName("test error");
        EventPublisher.publish(event);
    }

}
