package io.github.open.easykafka.client.support.retry;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.ErrorCode;
import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.producer.sender.ISender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 一批消息的重试器
 *
 * @author studeyang
 */
@Getter
@Slf4j
public class BatchMessageRetry implements RetryListener {

    private final ISender sender;
    private List<SendMessage> messages;
    private final RetryTemplate retryTemplate;

    public BatchMessageRetry(ISender sender, List<SendMessage> messages) {
        this.sender = sender;
        this.messages = messages;
        this.retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .exponentialBackoff(100, 2, 5000)
                .retryOn(ProducerException.class)
                .traversingCauses()
                .withListener(this)
                .build();
    }

    public List<MessageSendResult> trySend() {
        return retryTemplate.execute((RetryCallback<List<MessageSendResult>, ProducerException>) context -> {
                    // 执行发送逻辑
                    List<MessageSendResult> messageSendResult = sender.trySend(messages);

                    Set<String> successIds = messageSendResult.stream()
                            .filter(MessageSendResult::isSuccess)
                            .map(MessageSendResult::getMessageId)
                            .collect(Collectors.toSet());
                    List<SendMessage> sendFails = messages.stream()
                            .filter(sendMessage -> !successIds.contains(sendMessage.getMessageId()))
                            .collect(Collectors.toList());

                    // 发送失败的, 进行重试
                    if (!CollectionUtils.isEmpty(sendFails)) {
                        messages = sendFails;
                        throw new ProducerException(ErrorCode.PRODUCER_ERROR);
                    }
                    return messageSendResult;
                }
        );
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (!CollectionUtils.isEmpty(messages)) {
            log.error("发送失败, messageId: {}", messages.stream().map(SendMessage::getMessageId).collect(Collectors.joining(",")));
        }
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // 最后一次重试完成后，执行到这里
    }

}