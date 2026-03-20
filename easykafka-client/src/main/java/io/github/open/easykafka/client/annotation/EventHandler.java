package io.github.open.easykafka.client.annotation;

import io.github.open.easykafka.client.model.MessageConstant;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.annotation.*;

/**
 * 标注在类上，声明一个消息处理器。<br/><br/>
 *
 * <h3>基础用法：</h3>
 * <pre class="code">
 * &#064;Component
 * &#064;EventHandler(cluster = "send", topics = "easykafka-example-topic")
 * public class ExampleEventHandler implements IEventHandler {<br/>
 *     &#064;KafkaHandler
 *     public void handle(ExampleEvent event) {
 *         log.info("收到消息 message: {}", event);
 *     }<br/>
 * }
 * </pre>
 *
 * <h3>获取消息头字段：</h3>
 * <pre class="code">
 * &#064;Component
 * &#064;EventHandler(cluster = "send", topics = "easykafka-example-topic")
 * public class ExampleEventHandler implements IEventHandler {<br/>
 *     &#064;KafkaHandler
 *     public void handle(ExampleEvent event,
 *                        &#064;Header(value = "retryCount", required = false) ByteBuffer retryCount,
 *                        &#064;Header("name") String name {
 *         log.info("收到消息 message: {}, retryCount: {}, name: {}", event, retryCount.getInt(), name);
 *     }<br/>
 * }
 * </pre>
 *
 * <h3>消费同一 topic 下的不同消息类型：</h3>
 * <pre class="code">
 * &#064;Component
 * &#064;EventHandler(cluster = "send", topics = "easykafka-example-topic")
 * public class MultiEventHandler implements IEventHandler {<br/>
 *     &#064;KafkaHandler
 *     public void handle(ExampleEvent event) {
 *         log.info("收到消息 message: {}", event);
 *     }<br/>
 *     &#064;KafkaHandler
 *     public void handle(AnotherEvent event) {
 *         log.info("收到消息 message: {}", event);
 *     }<br/>
 * }
 * </pre>
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2024/8/29/029
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener
public @interface EventHandler {

    /**
     * topic所属集群
     */
    String cluster() default "";

    /**
     * 自动设值：同 groupId
     */
    @AliasFor(annotation = KafkaListener.class, attribute = "id")
    String id() default MessageConstant.DEFAULT_ID_VALUE;

    /**
     * topic名称
     */
    @AliasFor(annotation = KafkaListener.class, attribute = "topics")
    String topics() default "";

    /**
     * 自动设值：Send-Post.Biz-Post-RouteStaff
     */
    @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
    String groupId() default MessageConstant.DEFAULT_GROUP_ID_VALUE;

    @AliasFor(annotation = KafkaListener.class, attribute = "concurrency")
    String concurrency() default "1";

    /**
     * 自动设值：{cluster} + KafkaListenerContainerFactory
     */
    @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
    String containerFactory() default MessageConstant.DEFAULT_CONTAINER_FACTORY_VALUE;

    @AliasFor(annotation = KafkaListener.class, attribute = "properties")
    String[] properties() default {};

    /**
     * @deprecated (不可设值)
     */
    @Deprecated
    @AliasFor(annotation = KafkaListener.class, attribute = "errorHandler")
    String errorHandler() default MessageConstant.DEFAULT_ERROR_HANDLER_VALUE;

}