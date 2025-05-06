package io.github.open.easykafka.client.producer.sender;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.support.retry.BatchMessageRetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

/**
 * @author studeyang
 */
@Slf4j
public class RetryableSender implements ISender {

    private final ISender sender;

    public RetryableSender(ISender sender) {
        this.sender = sender;
    }

    @Override
    public MessageSendResult trySend(SendMessage message) {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .exponentialBackoff(100, 2, 5000)
                .retryOn(ProducerException.class)
                .traversingCauses()
                .build();
        return retryTemplate.execute((RetryCallback<MessageSendResult, ProducerException>) context ->
                sender.trySend(message)
        );
    }

    @Override
    public List<MessageSendResult> trySend(List<SendMessage> messages) {
        return new BatchMessageRetry(sender, messages).trySend();
    }

}
