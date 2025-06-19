package io.github.open.easykafka.client.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 消息常量
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstant {

    // -------------------------------- bean -------------------------------------------

    public static final String SPRING_CONTEXT_BEAN = "springContext";

    // -------------------------------- properties -------------------------------------------

    public static final String APPLICATION_NAME = "spring.application.name";
    public static final String EASYKAFKA_CONTAINER_FACTORY = "easykafka.containerFactory";


    // -------------------------------- consumer -------------------------------------------

    public static final String KAFKA_LISTENER_CONTAINER_FACTORY = "KafkaListenerContainerFactory";
    public static final String DEFAULT_GROUP_ID_VALUE = "#{__listener.groupId}";
    public static final String DEFAULT_ID_VALUE = "#{__listener.id}";
    public static final String DEFAULT_ERROR_HANDLER_VALUE = "#{__listener.errorHandler}";
    public static final String DEFAULT_CONTAINER_FACTORY_VALUE = "${" + EASYKAFKA_CONTAINER_FACTORY + "}";

}