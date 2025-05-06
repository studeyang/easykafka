package io.github.open.easykafka.example.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/21
 */
@Component
@Order
public class KafkaListenerMonitor implements ApplicationRunner {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Override
    public void run(ApplicationArguments args) {
        registry.getListenerContainers().forEach(container -> {
            Map<String, Object> info = new HashMap<>(4);
            info.put("listenerId", container.getListenerId());
            info.put("groupId", container.getGroupId());
            info.put("topics", String.join(",", container.getContainerProperties().getTopics()));
            info.put("isRunning", container.isRunning());

            System.out.println(info);
        });
    }
}
