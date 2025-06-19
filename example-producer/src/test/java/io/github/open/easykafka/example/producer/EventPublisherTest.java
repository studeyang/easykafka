package io.github.open.easykafka.example.producer;

import io.github.open.easykafka.client.EventPublisher;
import io.github.open.easykafka.event.AnotherTopicEvent;
import io.github.open.easykafka.event.Example2Event;
import io.github.open.easykafka.event.ExampleEvent;
import io.github.open.easykafka.event.SubExampleEvent;
import lombok.SneakyThrows;
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
@SpringBootTest(properties = "spring.profiles.active=fc")
public class EventPublisherTest {

    @After
    @SneakyThrows
    public void after() {
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void sendExampleEvent() {
        ExampleEvent event = new ExampleEvent();
        event.setName("test");
        event.setRetryCount(3);
        event.setOrderId(System.currentTimeMillis() + "");
        for (int i = 0; i < 1; i++) {
            EventPublisher.publish(event);
        }
    }

    @Test
    public void sendExample2Event() {
        Example2Event event = new Example2Event();
        event.setName("test");
        for (int i = 0; i < 10; i++) {
            EventPublisher.publish(event);
        }
    }

    @Test
    public void sendAnotherTopicExampleEvent() {
        AnotherTopicEvent event = new AnotherTopicEvent();
        event.setName("test");
        for (int i = 0; i < 10; i++) {
            EventPublisher.publish(event);
        }
    }

    @Test
    public void sendSubExampleEvent() {
        SubExampleEvent event = new SubExampleEvent();
        event.setName("test");
        event.setOrderId(System.currentTimeMillis() + "");
        event.setSub("sub");
        EventPublisher.publish(event);
    }

}
