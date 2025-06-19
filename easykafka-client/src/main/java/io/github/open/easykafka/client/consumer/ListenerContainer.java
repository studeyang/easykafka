package io.github.open.easykafka.client.consumer;

import com.alibaba.fastjson.JSON;
import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.ListenerMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/22
 */
@Slf4j
@RequiredArgsConstructor
public class ListenerContainer implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * [{"groupId":"", "topics":"", "cluster":"", "event":""}]
     */
    private static final Set<ListenerMetadata> LISTENER_SET = new HashSet<>();

    /**
     * groupId -> [com.example.Event1]
     */
    private static final Map<String, Set<String>> LISTENER_EVENT_MAP = new HashMap<>();

    /**
     * className -> EventHandler
     */
    private static final Map<String, EventHandler> CLASS_EVENTHANDLER_MAP = new HashMap<>();

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // SpringKafka
        List<Map<String, String>> kafkaListeners = new ArrayList<>();
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("listenerId", container.getListenerId());
            map.put("groupId", container.getGroupId());
            map.put("topics", String.join(",", Objects.requireNonNull(container.getContainerProperties().getTopics())));
            kafkaListeners.add(map);
        });
        String springKafkaLog = kafkaListeners.stream()
                .map(Map::toString)
                .collect(Collectors.joining("\n"));
        log.info("[SpringKafka] ListenerRegistry:\n{}", springKafkaLog);

        // EasyKafka
        String easyKafkaLog = LISTENER_SET.stream()
                .map(JSON::toJSONString)
                .sorted()
                .collect(Collectors.joining("\n"));
        log.info("[EasyKafka] ListenerContainer:\n{}", easyKafkaLog);
    }

    public void addListener(ListenerMetadata listenerMetadata) {
        LISTENER_SET.add(listenerMetadata);
    }

    public void putListenerEvent(String groupId, Method listenerMethod) {
        if (listenerMethod.getParameters().length == 0) {
            return;
        }
        String eventName = listenerMethod.getParameters()[0].getType().getName();
        LISTENER_EVENT_MAP.computeIfAbsent(groupId, k -> new HashSet<>()).add(eventName);
    }

    public void putEventHandler(String className, EventHandler eventHandler) {
        CLASS_EVENTHANDLER_MAP.put(className, eventHandler);
    }

    public static EventHandler getEventHandler(String className) {
        return CLASS_EVENTHANDLER_MAP.get(className);
    }

    public static boolean isListenOn(AbstractMessage message) {
        String groupId = KafkaUtils.getConsumerGroupId();
        Set<String> listenerEventSet = LISTENER_EVENT_MAP.get(groupId);
        return listenerEventSet != null && listenerEventSet.contains(message.getClass().getName());
    }

}
