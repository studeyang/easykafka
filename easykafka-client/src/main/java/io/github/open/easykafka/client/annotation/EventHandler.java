package io.github.open.easykafka.client.annotation;

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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener
public @interface EventHandler {

    /**
     * topic所属集群
     */
    String cluster() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "topics")
    String topics() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
    String groupId() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "concurrency")
    String concurrency() default "1";

    @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
    String containerFactory() default "";

    @AliasFor(annotation = KafkaListener.class, attribute = "properties")
    String[] properties() default {};

}