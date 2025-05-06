package io.github.open.easykafka.client.producer.sender;

import io.github.open.easykafka.client.model.SendMessage;

import java.util.List;

/**
 * 消息报告
 * @author studeyang
 */
public interface Reportable {

    /**
     * 在发送成功后的处理操作
     *
     * @param successMessage 发送成功的消息
     */
    void onSuccess(SendMessage successMessage);

    /**
     * 在发送成功后的处理操作
     *
     * @param successMessageList 发送成功的一批消息
     */
    void onSuccess(List<SendMessage> successMessageList);

    /**
     * 在重试失败后的处理操作
     *
     * @param failMessage 发送失败的消息
     */
    void onFail(SendMessage failMessage);

    /**
     * 在重试失败后的处理操作
     *
     * @param failMessages 发送失败的一批消息
     */
    void onFail(List<SendMessage> failMessages);

}
