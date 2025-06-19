package io.github.open.easykafka.client.support.converter;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.event.ExampleEvent;
import io.github.open.easykafka.client.message.Usage;
import io.github.open.easykafka.client.support.MessageIntrospector;
import io.github.open.easykafka.client.support.ObjectId;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.utils.JsonUtils;
import org.junit.Test;

import java.util.Date;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
public class JsonUtilsTest {

    @Test
    public void toJson() {
        ExampleEvent event = new ExampleEvent();
        event.setName("123");

        ExampleEvent.Sub sub = new ExampleEvent.Sub();
        sub.setSubName("0.1");
        event.setSub(sub);

        event.setMessageId(ObjectId.getId())
                .setMessageUsage(Usage.of(event))
                .setMessageService(SpringContext.getService())
                .setMessageTopic(MessageIntrospector.getTopic(ExampleEvent.class))
                .setMessageCreateTime(new Date());
        System.out.println(JsonUtils.toJson(event));
    }

    @Test
    public void toObject() {
        String json = "{\"@type\":\"io.github.open.easykafka.client.event.ExampleEvent\",\"id\":\"67ff910f020000010001b064\",\"name\":\"123\",\"sub\":{\"subName\":\"0.1\"},\"timeStamp\":\"2025-04-16 19:14:23\",\"usage\":\"EVENT\"}";
        System.out.println(JsonUtils.toObject(json, ExampleEvent.class));
    }

    @Test
    public void toObject_noSuchField() {
        String json = "{\"@type\":\"io.github.open.easykafka.client.event.ExampleEvent\",\"testNew\":\"tt\",\"id\":\"67ff910f020000010001b064\",\"name\":\"123\",\"sub\":{\"subName\":\"0.1\"},\"timeStamp\":\"2025-04-16 19:14:23\",\"usage\":\"EVENT\"}";
        System.out.println(JsonUtils.toObject(json, ExampleEvent.class));
    }

    @Test
    public void parse() {
        String json = "{\"@type\":\"io.github.open.easykafka.client.event.ExampleEvent\",\"id\":\"67ff910f020000010001b064\",\"name\":\"123\",\"sub\":{\"subName\":\"0.1\"},\"timeStamp\":\"2025-04-16 19:14:23\",\"usage\":\"EVENT\"}";
        Object object = JsonUtils.parse(json);
        System.out.println(object);
    }

    @Test
    public void testToObject() {
        String json = "{\"@type\":\"io.github.open.easykafka.client.event.ExampleEvent\",\"id\":\"67ff910f020000010001b064\",\"name\":\"123\",\"sub\":{\"subName\":\"0.1\"},\"timeStamp\":\"2025-04-16 19:14:23\",\"usage\":\"EVENT\"}";
        ExampleEvent event = JSON.parseObject(json, ExampleEvent.class);
        System.out.println(event);
    }
}