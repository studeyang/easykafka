package io.github.open.easykafka.example.producer;

import io.github.open.easykafka.client.EventPublisher;
import io.github.open.easykafka.client.message.Event;
import io.github.open.easykafka.example.event.BlankClusterEvent;
import io.github.open.easykafka.example.event.NoTopicAnnotationEvent;
import io.github.open.easykafka.example.event.NotSuchClusterEvent;
import junit.framework.TestCase;
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
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPublisherErrorTest extends TestCase {

    @After
    @SneakyThrows
    public void after() {
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void producerNotFound() {
        NotSuchClusterEvent event = new NotSuchClusterEvent();
        event.setName("test");
        EventPublisher.publish(event);
    }

    @Test
    public void producerNotFoundBatch() {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            NotSuchClusterEvent event = new NotSuchClusterEvent();
            event.setName("test");
            eventList.add(event);
        }
        EventPublisher.publish(eventList);
    }

    @Test
    public void noTopicAnnotation() {
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

}