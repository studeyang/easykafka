package io.github.open.easykafka.example.controller;

import io.github.open.easykafka.event.ExampleEvent;
import io.github.open.easykafka.client.EventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author studeyang
 */
@RestController
public class TestKafkaController {

    @RequestMapping("/sendKafka")
    public String send() {
        ExampleEvent exampleEvent = new ExampleEvent();
        exampleEvent.setName("测试easykafka");

        EventPublisher.publish(exampleEvent);
        return "success";
    }

}