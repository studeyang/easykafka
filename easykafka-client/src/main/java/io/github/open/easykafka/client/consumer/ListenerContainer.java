package io.github.open.easykafka.client.consumer;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.ListenerMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.support.KafkaUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/22
 */
@Slf4j
public class ListenerContainer implements ApplicationListener<ContextRefreshedEvent> {

    private static final List<ListenerMetadata> LISTENER_LIST = new ArrayList<>();
    /**
     * groupId -> [com.example.Event1]
     */
    private static final Map<String, Set<String>> LISTENER_EVENT_MAP = new HashMap<>();
    private static final Map<Class<?>, List<Method>> LISTENER_METHOD_MAP = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String listener = LISTENER_LIST.stream()
                .map(JSON::toJSONString)
                .collect(Collectors.joining("\n"));
        log.info("[EasyKafka] ListenerContainer:\n{}", listener);
    }

    public void addListener(ListenerMetadata listenerMetadata) {
        LISTENER_LIST.add(listenerMetadata);
    }

    public void putListenerMethod(Class<?> listenerClass, Method listenerMethod) {
        LISTENER_METHOD_MAP.computeIfAbsent(listenerClass, k -> new ArrayList<>()).add(listenerMethod);
    }

    public void putListenerEvent(String groupId, Method listenerMethod) {
        if (listenerMethod.getParameters().length > 0) {
            String eventName = listenerMethod.getParameters()[0].getType().getName();
            LISTENER_EVENT_MAP.computeIfAbsent(groupId, k -> new HashSet<>()).add(eventName);
        }
    }

    public static boolean isListenOn(AbstractMessage message) {
        String groupId = KafkaUtils.getConsumerGroupId();
        Set<String> listenerEventSet = LISTENER_EVENT_MAP.get(groupId);
        return listenerEventSet != null && listenerEventSet.contains(message.getClass().getName());
    }

}
