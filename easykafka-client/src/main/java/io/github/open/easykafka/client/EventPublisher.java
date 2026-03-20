package io.github.open.easykafka.client;

import io.github.open.easykafka.client.message.Event;
import io.github.open.easykafka.client.model.MessageMetadata;
import io.github.open.easykafka.client.model.MessageMetadataBuilder;
import io.github.open.easykafka.client.producer.MessagePublisher;
import io.github.open.easykafka.client.producer.callback.DefaultMessageCallback;
import io.github.open.easykafka.client.producer.callback.MessageCallback;
import io.github.open.easykafka.client.support.MessageIntrospector;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * <h3>这是一个事件发布器，使用示例：</h3>
 * <pre class="code">
 *     // 定义一条事件消息
 *     &#064;Topic(cluster="send", name="sendBoxOperateQueue")
 *     public class ClientPostEvent extends Event {
 *         &#064;MessageKey    // 指定发往 kafka 的 key
 *         private String orderId;
 *         &#064;MessageHeader // 指定消息头
 *         private Integer retryCount;
 *     }  <br/>
 *     // 发送这条消息
 *     ClientPostEvent event = new ClientPostEvent();
 *     event.setOrderId("123");
 *     event.setRetryCount(0);
 *     EventPublisher.publish(event);
 * </pre>
 *
 * <h3>使用说明：</h3>
 * <li>该发布器仅支持发送 {@link Event} 类型的消息。</li>
 * <li>如果您要发送的消息 topic 接入了基线和灰度，可使用该发布器自动选择环境 {@link io.github.open.easykafka.client.model.Tag}。</li>
 * <li>如果您只配置了集群的基线环境（{@link io.github.open.easykafka.client.model.Tag#BASE}），则该发布器只会发往基线环境。</li>
 * <li>如需选择性地发往指定的 {@link io.github.open.easykafka.client.model.Tag}，可使用 {@link ObjectPublisher}，但建议优先使用 EventPublisher 发布器。</li>
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @see ObjectPublisher
 * @since 1.0 2025/5/26
 */
@UtilityClass
@Slf4j
public final class EventPublisher {

    @Setter
    private static MessagePublisher publisher;

    /**
     * 发布一个事件
     */
    public static void publish(Event event) {
        publish(event, new DefaultMessageCallback());
    }

    /**
     * 发布一个事件，可传回调方法，使用示例：<br/><br/>
     *
     * <h3>发送成功回调：</h3>
     * <pre class="code">
     * SuccessCallback successCallback = () -> {
     *         System.out.println("发送消息成功了");
     *     };
     * EventPublisher.publish(new ExampleEvent(), successCallback);
     * </pre>
     *
     * <h3>发送失败回调：</h3>
     * <pre class="code">
     * FailCallback failCallback = e -> {
     *         if (e instanceof ProducerException) {
     *             System.out.println("消息发送失败了！");
     *         }
     * };
     * EventPublisher.publish(new ExampleEvent(), failCallback);
     * </pre>
     */
    public static void publish(Event event, MessageCallback messageCallback) {
        try {
            MessageMetadata metadata = new MessageMetadataBuilder()
                    .topicMetadata(MessageIntrospector.getTopic(event.getClass()))
                    .messageKey(MessageIntrospector.getMessageKey(event))
                    .messageHeaders(MessageIntrospector.getMessageHeaders(event))
                    .build();
            publisher.publish(event, metadata, messageCallback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发布一批事件
     */
    public static void publish(Collection<? extends Event> events) {
        publisher.publish(events, new DefaultMessageCallback());
    }

}