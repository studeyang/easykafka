package io.github.open.easykafka.client.producer;

import io.github.open.easykafka.client.message.AbstractMessage;
import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.model.SendMessage;
import io.github.open.easykafka.client.producer.sender.ISender;
import io.github.open.easykafka.client.support.converter.SendMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 默认的消息发布器, 消息将直接发送到Producer
 *
 * @author studeyang
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultMessagePublisher implements MessagePublisher {

    private final ISender sender;
    private final ExecutorService executorService;

    @Override
    public void publish(Object message, MessageMetadata messageMetadata) {
        executorService.execute(() -> {
            try {
                SendMessage sendMessage = SendMessageConverter.convert(message, messageMetadata);
                sender.trySend(sendMessage);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void publish(Collection<? extends AbstractMessage> messages) {
        executorService.execute(() -> {
            try {
                List<SendMessage> sendMessageList = SendMessageConverter.convert(messages);
                sender.trySend(sendMessageList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
