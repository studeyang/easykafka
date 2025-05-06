package io.github.open.easykafka.example;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.message.Event;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.producer.sender.ISender;
import io.github.open.easykafka.client.support.converter.SendMessageConverter;
import io.github.open.easykafka.example.event.NormalEvent;
import io.github.open.easykafka.example.event.NotSuchClusterEvent;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 这里不是异步执行，好测一点
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultMessagePublisherTest extends TestCase {

    @Autowired
    private ISender reportableSender;

    @Test(expected = ProducerException.class)
    public void testRetry() {
        NotSuchClusterEvent event = new NotSuchClusterEvent();
        event.setName("test");

        SendMessage sendMessage = SendMessageConverter.convert(event);
        reportableSender.trySend(sendMessage);
    }

    @Test(expected = ProducerException.class)
    public void testRetryBatch() {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            NotSuchClusterEvent event = new NotSuchClusterEvent();
            event.setName("test");
            eventList.add(event);
        }

        List<SendMessage> sendMessageList = eventList.stream()
                .map(SendMessageConverter::convert)
                .collect(Collectors.toList());
        reportableSender.trySend(sendMessageList);
    }

    @Test
    public void sendNormal() {
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            NormalEvent event = new NormalEvent();
            event.setName("test");
            eventList.add(event);
        }

        List<SendMessage> sendMessageList = eventList.stream()
                .map(SendMessageConverter::convert)
                .collect(Collectors.toList());
        reportableSender.trySend(sendMessageList);
    }

}