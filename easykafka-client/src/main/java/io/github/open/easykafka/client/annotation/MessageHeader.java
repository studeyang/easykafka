package io.github.open.easykafka.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Message的字段上, 表示消息 Header
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MessageHeader {

}