package io.github.open.easykafka.client.message;

import io.github.open.easykafka.client.annotation.EventHandler;
import io.github.open.easykafka.client.consumer.ListenerContainer;
import io.github.open.easykafka.client.model.MessageConstant;
import io.github.open.easykafka.client.support.SpringContext;

import static io.github.open.easykafka.client.model.MessageConstant.DEFAULT_GROUP_ID_VALUE;
import static io.github.open.easykafka.client.model.MessageConstant.EASYKAFKA_CONTAINER_FACTORY;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/5/15
 */
public interface IEventHandler {

    /**
     * 获取 listenerId
     * @return listenerId
     */
    default String getId() {

        EventHandler eventHandler = ListenerContainer.getEventHandler(this.getClass().getName());

        // 设置默认的 containerFactory 值
        System.setProperty(EASYKAFKA_CONTAINER_FACTORY,
                eventHandler.cluster() + MessageConstant.KAFKA_LISTENER_CONTAINER_FACTORY);

        if (DEFAULT_GROUP_ID_VALUE.equals(eventHandler.groupId())) {
            return getGroupId();
        }
        return eventHandler.groupId() + "|" + eventHandler.topics();
    }

    /**
     * 获取 groupId
     * @return groupId
     */
    default String getGroupId() {
        EventHandler eventHandler = ListenerContainer.getEventHandler(this.getClass().getName());
        return SpringContext.getGroupIdPrefix() + eventHandler.topics();
    }

    /**
     * 移除同 EventHandler 下的 containerFactory 属性值
     * @return 空值
     */
    default String getErrorHandler() {
        System.setProperty(EASYKAFKA_CONTAINER_FACTORY, "");
        return "";
    }

}
