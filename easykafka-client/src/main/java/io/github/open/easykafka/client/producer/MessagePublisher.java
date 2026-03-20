package io.github.open.easykafka.client.producer;

import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.producer.callback.MessageCallback;

import java.util.Collection;

/**
 * 消息发布器
 * @author <a href="https://github.com/studeyang">studeyang</a>
 */
public interface MessagePublisher {

    /**
     * 发布一个消息
     *
     * @param message         消息
     * @param messageMetadata 消息元数据
     * @param messageCallback 消息回调
     */
    void publish(Object message, MessageMetadata messageMetadata, MessageCallback messageCallback);

    /**
     * 发布一批消息
     *
     * @param messages        消息集合
     * @param messageCallback 消息回调
     */
    void publish(Collection<? extends AbstractMessage> messages, MessageCallback messageCallback);

}
