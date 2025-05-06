package io.github.open.easykafka.client.producer.sender;

import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;

import java.util.List;

/**
 * 请求发送器
 * @author studeyang
 */
public interface ISender {

    /**
     * 尝试发送，可能会失败
     *
     * @param message 消息
     * @return 发送结果
     */
    MessageSendResult trySend(SendMessage message);

    /**
     * 尝试发送，可能会失败
     *
     * @param messages 一批消息
     * @return 发送结果
     */
    List<MessageSendResult> trySend(List<SendMessage> messages);

}
