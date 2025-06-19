package io.github.open.easykafka.client.annotation;

import io.github.open.easykafka.client.model.MessageConstant;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.annotation.*;

/**
 * 表示该类是一个消息处理器
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