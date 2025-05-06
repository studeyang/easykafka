package io.github.open.easykafka.client.producer.sender;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.exception.ValidationException;
import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.producer.ProducerContainer;
import io.github.open.easykafka.client.producer.StringKafkaProducer;
import io.github.open.easykafka.client.support.converter.ExceptionConverter;
import io.github.open.easykafka.client.model.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
@Slf4j
@RequiredArgsConstructor
public class ProducerSender implements ISender {

    private static final String SEND_ERROR = "kafka发送消息失败, cause:{}";
    private static final long TIMEOUT = 5000;
    /**
     * 批量发送时, 每条消息最多的超时时间
     */
    private static final long MESSAGE_MAX_TIMEOUT = 500;

    private final ProducerContainer producerContainer;

    /**
     * 每批发送的集合大小
     */
    @Getter
    private final int partitionSize;

    @Override
    public MessageSendResult trySend(SendMessage message) {

        validate(message);
        ProducerRecord<String, String> producerRecord = creatRecord(message);

        try {
            StringKafkaProducer producer = producerContainer.getProducer(message.getTopic().getCluster());
            producer.send(producerRecord).get(TIMEOUT, TimeUnit.MILLISECONDS);
            return MessageSendResult.success(message.getMessageId());
        } catch (ProducerException e) {
            log.warn(SEND_ERROR, e.getMessage(), e);
            throw e;
        } catch (InterruptedException e) {
            log.warn(SEND_ERROR, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new ProducerException(ErrorCode.PRODUCER_ERROR);
        } catch (Exception e) {
            log.warn(SEND_ERROR, e.getMessage(), e);
            throw new ProducerException(ErrorCode.PRODUCER_ERROR);
        }
    }

    @Override
    public List<MessageSendResult> trySend(List<SendMessage> messages) {

        // 校验消息
        messages.forEach(this::validate);

        return messages.size() <= partitionSize ? doSend(messages) : ListUtils.partition(messages, partitionSize)
                .stream()
                .map(this::doSend)
                .collect(ArrayList::new, List::addAll, List::addAll);
    }

    private List<MessageSendResult> doSend(List<SendMessage> messages) {
        try {
            return doSendBatch(messages);
        } catch (Exception e) {
            log.warn(SEND_ERROR, e.getMessage(), e);
            return messages.stream()
                    .map(m -> MessageSendResult.error(m.getMessageId(), ExceptionConverter.getCause(e)))
                    .collect(Collectors.toList());
        }
    }

    private List<MessageSendResult> doSendBatch(List<SendMessage> messages) {
        List<MessageSendResult> toReturnList = new ArrayList<>();

        Map<String, SendMessage> allMessageMap = messages.stream()
                .collect(Collectors.toMap(SendMessage::getMessageId, Function.identity()));

        Map<String, Future<RecordMetadata>> futureMap = new HashMap<>(8);
        // 每个消息先发送, 获取Future
        for (SendMessage message : messages) {
            ProducerRecord<String, String> producerRecord = creatRecord(message);
            try {
                StringKafkaProducer producer = producerContainer.getProducer(messages.get(0).getTopic().getCluster());
                futureMap.put(message.getMessageId(), producer.send(producerRecord));
            } catch (Exception e) {
                log.warn("批量发送kafka失败: {}, message:{}", e.getMessage(), message);
                toReturnList.add(MessageSendResult.error(message.getMessageId(), ExceptionConverter.getCause(e)));
            }
        }

        long deadline = System.currentTimeMillis() + TIMEOUT;
        long timeout = TIMEOUT;
        // 每个Future都get, 时间逐步减少, 整个过程不会超过 5000ms (TIMEOUT)
        for (Map.Entry<String, Future<RecordMetadata>> entry : futureMap.entrySet()) {
            String messageId = entry.getKey();
            Future<RecordMetadata> feature = entry.getValue();
            if (timeout <= 0) {
                toReturnList.add(MessageSendResult.error(messageId, "发送kafka消息超时"));
            } else {
                try {
                    feature.get(Math.min(timeout, MESSAGE_MAX_TIMEOUT), TimeUnit.MILLISECONDS);
                    toReturnList.add(MessageSendResult.success(messageId));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    toReturnList.add(MessageSendResult.error(messageId, ExceptionConverter.getCause(e)));
                } catch (Exception e) {
                    log.error("批量发送kafka超时: {}, message:{}", e.getMessage(), allMessageMap.get(messageId));
                    toReturnList.add(MessageSendResult.error(messageId, ExceptionConverter.getCause(e)));
                }
                timeout = refreshTimeout(deadline);
            }
        }
        return toReturnList;
    }

    private long refreshTimeout(long deadline) {
        return deadline - System.currentTimeMillis();
    }

    private void validate(SendMessage message) {
        assertNotNull(message, "消息不能为空");
        assertHasText(message.getMessageId(), "messageId不能为空");
        assertHasText(message.getTopic().getCluster(), "cluster不能为空");
        assertHasText(message.getTopic().getName(), "topic不能为空");
        assertHasText(message.getType(), "type不能为空");
        assertHasText(message.getService(), "service不能为空");
        assertHasText(message.getContent(), "content不能为空");
        assertNotNull(message.getCreatedAt(), "createdAt不能为空");
        assertNotNull(message.getUsage(), "usage不能为空");
    }

    private void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    private void assertHasText(String object, String message) {
        if (!StringUtils.hasText(object)) {
            throw new ValidationException(message);
        }
    }

    private ProducerRecord<String, String> creatRecord(SendMessage message) {
        List<Header> headers = new ArrayList<>();
        return new ProducerRecord<>(
                message.getTopic().getName(),
                null,
                message.getCreatedAt().getTime(),
                message.getMessageId(),
                message.getContent(),
                headers);
    }

}
