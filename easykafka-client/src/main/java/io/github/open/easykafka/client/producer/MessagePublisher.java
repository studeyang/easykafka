package io.github.open.easykafka.client.producer;

import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.MessageMetadata;

import java.util.Collection;

/**
 * 消息发布器
 * @author studeyang
 */
public interface MessagePublisher {

    /**
     * 发布一个消息
     *
     * @param message         消息
     * @param messageMetadata 消息元数据
     */
    void publish(Object message, MessageMetadata messageMetadata);

    /**
     * 发布一批消息
     *
     * @param messages 消息集合
     */
    void publish(Collection<? extends AbstractMessage> messages);

}
