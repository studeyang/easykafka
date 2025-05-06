package io.github.open.easykafka.client.message;

import io.github.open.easykafka.client.exception.ProducerException;
import io.github.open.easykafka.client.model.ErrorCode;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
public enum Usage {

    /**
     * 事件
     */
    EVENT,

    /**
     * 广播
     */
    BROADCAST,

    /**
     * 数据同步
     */
    DATA_SYNC;

    /**
     * 默认null为事件
     */
    public static Usage of(String name) {
        return name == null ? EVENT : Usage.valueOf(name);
    }

    /**
     * 根据类型判断是event还是broadcast
     */
    public static Usage of(AbstractMessage message) {
        if (message instanceof Event) {
            return EVENT;
        } else if (message instanceof Broadcast) {
            return BROADCAST;
        } else if (message instanceof DataSync) {
            return DATA_SYNC;
        }
        throw new ProducerException(ErrorCode.UNRECOGNIZED_USAGE);
    }
}