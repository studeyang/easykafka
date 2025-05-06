package io.github.open.easykafka.client.support.converter;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.message.Usage;
import io.github.open.easykafka.client.model.ErrorCode;
import io.github.open.easykafka.client.model.MessageSendResult;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.support.SpringContext;
import io.github.open.easykafka.client.support.id.ObjectId;
import io.github.open.easykafka.client.support.utils.JsonUtils;
import io.github.open.easykafka.client.support.utils.MessageMetadataGetter;
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
     * @param message 消息
     */
    public static SendMessage convert(AbstractMessage message) {
        return convert(message, new Date());
    }

    /**
     * Message转换为SendMessage
     *
     * @param messages 一批消息
     */
    public static List<SendMessage> convert(Collection<? extends AbstractMessage> messages) {
        Date now = new Date();
        return messages.stream().map(m -> convert(m, now)).collect(Collectors.toList());
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

    private static SendMessage convert(AbstractMessage message, Date date) {

        if (message == null) {
            throw new ProducerException(ErrorCode.MESSAGE_MUST_NOTNULL);
        }

        Class<?> clazz = message.getClass();

        message.setId(ObjectId.getId())
                .setUsage(Usage.of(message))
                .setService(SpringContext.getService())
                .setTopic(MessageMetadataGetter.getTopic(clazz))
                .setTimeStamp(date);

        return new SendMessage()
                .setMessageId(message.getId())
                .setTopic(message.getTopic())
                .setType(clazz.getName())
                .setService(message.getService())
                .setContent(JsonUtils.toJson(message))
                .setCreatedAt(message.getTimeStamp())
                .setUsage(message.getUsage());
    }
}
