package io.github.open.easykafka.client.support.converter;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.message.Usage;
import io.github.open.easykafka.client.model.*;
import io.github.open.easykafka.client.support.MessageIntrospector;
import io.github.open.easykafka.client.support.ObjectId;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 发送消息转换器
 *
 * @author studeyang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendMessageConverter {

    /**
     * Message转换为SendMessage
     *
     * @param message         消息
     * @param messageMetadata 消息元数据
     */
    public static SendMessage convert(Object message, MessageMetadata messageMetadata) {
        if (messageMetadata.getMessageHeaders() != null) {
            messageMetadata.getMessageHeaders().values().forEach(MessageIntrospector::checkMessageHeaderType);
        }
        return convert(message, new Date(), messageMetadata);
    }

    /**
     * Message转换为SendMessage
     *
     * @param messages 一批消息
     */
    public static List<SendMessage> convert(Collection<? extends AbstractMessage> messages) {
        Date now = new Date();
        return messages.stream()
                .map(m -> {
                    MessageMetadata metadata = new MessageMetadataBuilder()
                            .topicMetadata(MessageIntrospector.getTopic(m.getClass()))
                            .messageKey(MessageIntrospector.getMessageKey(m))
                            .messageHeaders(MessageIntrospector.getMessageHeaders(m))
                            .build();
                    return convert(m, now, metadata);
                })
                .collect(Collectors.toList());
    }

    /**
     * 将消息按发送结果分类
     *
     * @param messages    消息
     * @param sendResults 消息发送结果
     * @return 结果成功/失败分类, key: false(失败)
     */
    public static Map<Boolean, List<SendMessage>> classify(List<SendMessage> messages,
                                                           List<MessageSendResult> sendResults) {

        Set<String> successIds = sendResults.stream()
                .filter(MessageSendResult::isSuccess)
                .map(MessageSendResult::getMessageId)
                .collect(Collectors.toSet());

        return messages.stream()
                .collect(Collectors.partitioningBy(m -> successIds.contains(m.getMessageId())));
    }

    private static SendMessage convert(Object message, Date date, MessageMetadata messageMetadata) {

        if (message == null) {
            throw new ProducerException(ErrorCode.MESSAGE_MUST_NOTNULL);
        }

        Class<?> clazz = message.getClass();
        String messageId = ObjectId.getId();
        Usage usage = Usage.of(message);
        String service = SpringContext.getService();
        TopicMetadata topic = messageMetadata.getTopicMetadata();
        String messageKey = messageMetadata.getMessageKey();
        Map<String, Object> messageHeaders = messageMetadata.getMessageHeaders();

        if (message instanceof AbstractMessage) {
            ((AbstractMessage) message).setMessageId(messageId)
                    .setMessageKey(messageKey)
                    .setMessageHeader(messageHeaders)
                    .setMessageTag(messageMetadata.getMessageTag())
                    .setMessageUsage(usage)
                    .setMessageService(service)
                    .setMessageTopic(topic)
                    .setMessageCreateTime(date);
        }

        return new SendMessage()
                .setMessageId(messageId)
                .setMessageKey(messageKey)
                .setMessageHeader(messageHeaders)
                .setMessageTag(messageMetadata.getMessageTag())
                .setTopic(topic)
                .setType(clazz.getName())
                .setService(service)
                .setContent(JsonUtils.toJson(message))
                .setCreatedAt(date)
                .setUsage(usage);
    }

}
