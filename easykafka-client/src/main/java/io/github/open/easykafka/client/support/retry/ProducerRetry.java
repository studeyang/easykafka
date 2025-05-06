package io.github.open.easykafka.client.support.retry;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.MessageSendResult;
import org.springframework.retry.RetryCallback;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/18
 */
public interface ProducerRetry extends RetryCallback<MessageSendResult, ProducerException> {
}
