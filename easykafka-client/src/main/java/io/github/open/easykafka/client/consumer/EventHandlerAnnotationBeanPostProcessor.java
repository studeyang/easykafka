package io.github.open.easykafka.client.consumer;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.model.ListenerMetadata;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.model.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/22
 */
@Slf4j
public class EventHandlerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ListenerContainer listenerContainer;

    public EventHandlerAnnotationBeanPostProcessor(ListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);

            if (eventHandler != null) {
                // 设置 @EventHandler 默认属性
                if (!StringUtils.hasText(eventHandler.cluster())) {
                    modifyAnnotationValue(eventHandler, "cluster", getDefaultCluster(method));
                }
                if (!StringUtils.hasText(eventHandler.topics())) {
                    modifyAnnotationValue(eventHandler, "topics", getDefaultTopics(method));
                }
                if (!StringUtils.hasText(eventHandler.groupId())) {
                    modifyAnnotationValue(eventHandler, "groupId", getDefaultGroupId());
                }
                if (!StringUtils.hasText(eventHandler.containerFactory())) {
                    modifyAnnotationValue(eventHandler, "containerFactory", getDefaultContainerFactory(eventHandler));
                }

                // 添加 Listener 至 ListenerContainer
                addListener(method, eventHandler);
                listenerContainer.putListenerMethod(bean.getClass(), method);
                listenerContainer.putListenerEvent(eventHandler.groupId(), method);
            }
        });

        return bean;
    }

    private String getDefaultGroupId() {
        return Optional.ofNullable(SpringContext.getService())
                .orElseThrow(() -> new IllegalStateException("Can't get default groupId"));
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

    private void addListener(Method method, EventHandler eventHandler) {
        ListenerMetadata listenerMetadata = new ListenerMetadata();
        listenerMetadata.setGroupId(eventHandler.groupId());
        listenerMetadata.setTopics(eventHandler.topics());
        listenerMetadata.setCluster(eventHandler.cluster());
        listenerMetadata.setEvent(method.getParameters()[0].getType().getName());

        listenerContainer.addListener(listenerMetadata);
    }

    private String getDefaultContainerFactory(EventHandler eventHandler) {
        if (SpringContext.isGrayEnvironment()) {
            return eventHandler.cluster() + "Gray" + MessageConstant.KAFKA_LISTENER_CONTAINER_FACTORY;
        }
        return eventHandler.cluster() + MessageConstant.KAFKA_LISTENER_CONTAINER_FACTORY;
    }

    private void modifyAnnotationValue(EventHandler annotation, String attribute, Object newValue) {
        try {
            // 获取注解的代理处理器
            Object handler = Proxy.getInvocationHandler(annotation);

            // 获取成员值字段
            Field memberValuesField = handler.getClass().getDeclaredField("memberValues");
            ReflectionUtils.makeAccessible(memberValuesField);

            // 修改属性值
            @SuppressWarnings("unchecked")
            Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(handler);
            memberValues.put(attribute, newValue);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to modify EventHandler annotation", e);
        }
    }

}
