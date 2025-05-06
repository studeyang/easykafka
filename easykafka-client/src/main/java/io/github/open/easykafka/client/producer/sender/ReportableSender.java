package io.github.open.easykafka.client.producer.sender;

import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.support.converter.SendMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author yanglulu
 */
@Slf4j
@RequiredArgsConstructor
public class ReportableSender implements ISender, Reportable {

    private final ISender sender;

    @Override
    public MessageSendResult trySend(SendMessage message) {

        try {
            MessageSendResult result = sender.trySend(message);
            if (result.isSuccess()) {
                onSuccess(message);
            } else {
                onFail(message);
            }
            return result;
        } catch (Exception e) {
            onFail(message);
            throw e;
        }
    }

    @Override
    public List<MessageSendResult> trySend(List<SendMessage> messages) {

        List<MessageSendResult> sendResults = sender.trySend(messages);
        Map<Boolean, List<SendMessage>> messagesMap = SendMessageConverter.classify(messages, sendResults);

        onSuccess(messagesMap.get(true));
        onFail(messagesMap.get(false));

        return sendResults;
    }

    @Override
    public void onSuccess(SendMessage successMessage) {
        if (successMessage != null) {
            log.info("kafka发送消息成功, message:{}", successMessage.getContent());
        }
    }

    @Override
    public void onSuccess(List<SendMessage> successMessageList) {
        if (CollectionUtils.isNotEmpty(successMessageList)) {
            for (int i = 0; i < successMessageList.size(); i++) {
                log.info("kafka发送批量消息成功, message-{}: {}", i + 1, successMessageList.get(i).getContent());
            }
        }
    }

    @Override
    public void onFail(SendMessage failMessage) {
        if (failMessage != null) {
            log.error("kafka发送消息失败, message:{}", failMessage.getContent());
        }
    }

    @Override
    public void onFail(List<SendMessage> failMessages) {
        if (CollectionUtils.isNotEmpty(failMessages)) {
            for (int i = 0; i < failMessages.size(); i++) {
                log.error("kafka发送批量消息失败, message-{}: {}", i + 1, failMessages.get(i).getContent());
            }
        }
    }
}
