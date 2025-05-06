package io.github.open.easykafka.client.annotation;

import java.lang.annotation.*;

/**
 * 标记在消息的类上, 用于指定该消息发往哪个topic
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2024/8/29/029
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {

    String cluster();

    /**
     * topic
     */
    String name();
}
