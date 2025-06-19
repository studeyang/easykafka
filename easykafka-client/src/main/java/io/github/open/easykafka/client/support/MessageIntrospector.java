package io.github.open.easykafka.client.support;

import io.github.open.easykafka.client.annotation.MessageHeader;
import io.github.open.easykafka.client.annotation.MessageKey;
import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.ErrorCode;
import io.github.open.easykafka.client.model.TopicMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息内省, 用于获取Message的自身属性
 *
 * @author 005964
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageIntrospector {

    /**
     * topic缓存
     */
    private static final Map<Class<? extends AbstractMessage>, TopicMetadata> TOPIC_CACHE = new ConcurrentHashMap<>();
    /**
     * messageKeyField缓存
     */
    private static final Map<Class<? extends AbstractMessage>, Field> MESSAGE_KEY_FIELD_CACHE = new ConcurrentHashMap<>();
    /**
     * messageHeaderField缓存
     */
    private static final Map<Class<? extends AbstractMessage>, List<Field>> MESSAGE_HEADER_FIELD_CACHE = new ConcurrentHashMap<>();

    private static final Set<Class<?>> MESSAGE_HEADER_SUPPORT_TYPES = new HashSet<>();

    static {
        MESSAGE_HEADER_SUPPORT_TYPES.add(String.class);
        MESSAGE_HEADER_SUPPORT_TYPES.add(Integer.class);
    }

    /**
     * 获取topic
     */
    public static TopicMetadata getTopic(Class<? extends AbstractMessage> clazz) {
        return TOPIC_CACHE.computeIfAbsent(clazz, MessageIntrospector::getTopicFromAnnotation);
    }

    /**
     * 获取message的sequenceKey
     */
    public static String getMessageKey(AbstractMessage message) {

        Field messageKeyField = MESSAGE_KEY_FIELD_CACHE.computeIfAbsent(message.getClass(), MessageIntrospector::getMessageKeyField);

        if (messageKeyField == null) {
            return null;
        }

        return Objects.toString(getFieldValue(messageKeyField, message), null);
    }

    private static Object getFieldValue(Field field, AbstractMessage message) {
        try {
            return field.get(message);
        } catch (ReflectiveOperationException e) {
            log.error("反射获取Field的值失败, Field:{}, message:{}", field, message, e);
            return null;
        }
    }

    public static Map<String, Object> getMessageHeaders(AbstractMessage message) {
        List<Field> messageHeaderFields = MESSAGE_HEADER_FIELD_CACHE.computeIfAbsent(
                message.getClass(), MessageIntrospector::getMessageHeaderFields);

        Map<String, Object> messageHeaderMap = new HashMap<>(8);

        for (Field messageHeaderField : messageHeaderFields) {

            Object value = getFieldValue(messageHeaderField, message);

            if (value != null) {
                messageHeaderMap.put(messageHeaderField.getName(), value);
            }
        }

        return messageHeaderMap;
    }

    /**
     * 获取带有@MessageKey的Field
     */
    private static Field getMessageKeyField(Class<? extends AbstractMessage> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(MessageKey.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            }
        }
        return null;
    }

    /**
     * 获取带有@MessageHeader的Field
     */
    private static List<Field> getMessageHeaderFields(Class<? extends AbstractMessage> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(MessageHeader.class)) {

                checkMessageHeaderType(clazz, field);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fields.add(field);
            }
        }
        return fields;
    }

    public static void checkMessageHeaderType(Object object) {
        if (!MESSAGE_HEADER_SUPPORT_TYPES.contains(object.getClass())) {
            String message = String.format("Type=[%s] Is Not Support!", object.getClass().getName());
            throw new ProducerException(ErrorCode.UNSUPPORTED_MESSAGE_HEADER_TYPE, message);
        }
    }

    private static void checkMessageHeaderType(Class<? extends AbstractMessage> clazz, Field field) {
        if (!MESSAGE_HEADER_SUPPORT_TYPES.contains(field.getType())) {
            String message = String.format("Type=[%s] Is Not Support! Check @MessageHeader On Class=[%s] Field=[%s]",
                    field.getType().getName(), clazz.getName(), field.getName());
            throw new ProducerException(ErrorCode.UNSUPPORTED_MESSAGE_HEADER_TYPE, message);
        }
    }

    /**
     * 从Class中获取topic
     */
    private static TopicMetadata getTopicFromAnnotation(Class<? extends AbstractMessage> clazz) {

        Topic topic = clazz.getAnnotation(Topic.class);

        if (null == topic || !StringUtils.hasText(topic.name())) {
            throw new ProducerException(ErrorCode.NOT_TOPIC);
        }

        return new TopicMetadata(topic.cluster(), topic.name());
    }

}
