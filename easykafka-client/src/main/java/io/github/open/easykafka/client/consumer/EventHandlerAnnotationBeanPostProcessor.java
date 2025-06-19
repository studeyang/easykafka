package io.github.open.easykafka.client.consumer;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.model.ListenerMetadata;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.utils.AnnotationModifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.open.easykafka.client.model.MessageConstant.*;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/22
 */
@Slf4j
public class EventHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final String CLUSTER_ATTRIBUTE = "cluster";
    private static final String TOPICS_ATTRIBUTE = "topics";

    private final ListenerContainer listenerContainer;
    /**
     * Send-Post.easykafka-example-topic -> counter
     */
    private final Map<String, AtomicInteger> counterMap = new HashMap<>();

    public EventHandlerAnnotationBeanPostProcessor(ListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        EventHandler eventHandler = AnnotationUtils.findAnnotation(bean.getClass(), EventHandler.class);
        if (eventHandler != null) {
            Assert.hasText(eventHandler.cluster(), "cluster不可为空");
            Assert.hasText(eventHandler.topics(), "topics不可为空");
            Assert.hasText(eventHandler.containerFactory(), "containerFactory不可为空");

            Assert.isTrue(DEFAULT_ID_VALUE.equals(eventHandler.id()), "@EventHandler id不支持设值");

            Set<Method> methodsWithHandler = MethodIntrospector.selectMethods(bean.getClass(),
                    (ReflectionUtils.MethodFilter) method ->
                            AnnotationUtils.findAnnotation(method, KafkaHandler.class) != null);
            for (Method method : methodsWithHandler) {
                addListener(bean.getClass(), method, eventHandler);
            }
        }

        processMethodLevelListeners(bean);

        return bean;
    }

    private void processMethodLevelListeners(Object bean) {

        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);

            if (eventHandler != null) {

                // 设置 @EventHandler 默认属性
                if (!StringUtils.hasText(eventHandler.cluster())) {
                    AnnotationModifyUtils.modifyMethodAnnotation(eventHandler, CLUSTER_ATTRIBUTE, getDefaultCluster(method));
                }

                if (!StringUtils.hasText(eventHandler.topics())) {
                    AnnotationModifyUtils.modifyMethodAnnotation(eventHandler, TOPICS_ATTRIBUTE, getDefaultTopics(method));
                }

                // 添加 Listener 至 ListenerContainer
                addListener(bean.getClass(), method, eventHandler);
            }
        });
    }

    private String getDefaultTopics(Method method) {
        return Optional.ofNullable(method.getParameters())
                .filter(parameters -> parameters.length > 0)
                .map(parameters -> parameters[0].getType())
                .map(eventClass -> eventClass.getAnnotation(Topic.class))
                .map(Topic::name)
                .orElseThrow(() -> new IllegalStateException("Can't get default topic"));
    }

    private String getDefaultCluster(Method method) {
        return Optional.ofNullable(method.getParameters())
                .filter(parameters -> parameters.length > 0)
                .map(parameters -> parameters[0].getType())
                .map(eventClass -> eventClass.getAnnotation(Topic.class))
                .map(Topic::cluster)
                .orElseThrow(() -> new IllegalStateException("Can't get default cluster"));
    }

    private void addListener(Class<?> listenerClass, Method method, EventHandler eventHandler) {

        String groupId = DEFAULT_GROUP_ID_VALUE.equals(eventHandler.groupId()) ?
                getDefaultGroupId(eventHandler) : eventHandler.groupId();

        listenerContainer.putEventHandler(listenerClass.getName(), eventHandler);
        listenerContainer.putListenerEvent(groupId, method);

        ListenerMetadata listenerMetadata = new ListenerMetadata();
        listenerMetadata.setGroupId(groupId);
        listenerMetadata.setTopics(eventHandler.topics());
        listenerMetadata.setCluster(eventHandler.cluster());
        listenerMetadata.setEvent(method.getParameters()[0].getType().getName());

        listenerContainer.addListener(listenerMetadata);
    }

    public String getDefaultId(EventHandler eventHandler) {
        String groupId = getDefaultGroupId(eventHandler);
        int count = counterMap.computeIfAbsent(groupId, k -> new AtomicInteger()).getAndIncrement();
        return groupId + "#" + count;
    }

    private String getDefaultGroupId(EventHandler eventHandler) {
        return SpringContext.getGroupIdPrefix() + eventHandler.topics();
    }

    private String getDefaultContainerFactory(EventHandler eventHandler) {
        return eventHandler.cluster() + KAFKA_LISTENER_CONTAINER_FACTORY;
    }

}
