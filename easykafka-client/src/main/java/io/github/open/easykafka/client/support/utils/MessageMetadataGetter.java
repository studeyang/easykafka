package io.github.open.easykafka.client.support.utils;

import io.github.open.easykafka.client.annotation.Topic;
import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.ErrorCode;
import io.github.open.easykafka.client.model.TopicMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息内省, 用于获取Message的自身属性
 * @author studeyang
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageMetadataGetter {

    /**
     * topic缓存
     */
    private static final Map<Class<?>, TopicMetadata> TOPIC_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取topic
     */
    public static TopicMetadata getTopic(Class<?> clazz) {
        return TOPIC_CACHE.computeIfAbsent(clazz, MessageMetadataGetter::getTopicFromAnnotation);
    }

    /**
     * 从Class中获取topic
     */
    private static TopicMetadata getTopicFromAnnotation(Class<?> clazz) {

        Topic topic = clazz.getAnnotation(Topic.class);

        if (null == topic || !StringUtils.hasText(topic.name())) {
            throw new ProducerException(ErrorCode.NOT_TOPIC);
        }

        return new TopicMetadata(topic.cluster(), topic.name());
    }

}
