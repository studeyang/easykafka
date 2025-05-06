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

    /**
     * 消息json中表示Message实际类型的字段
     */
    public static final String MESSAGE_TYPE = "jsonMessageType";

    public static final String CLUSTER = "cluster";

    public static final String ENV = "env";

    // -------------------------------- bean -------------------------------------------

    public static final String PRODUCER_EXECUTOR_BEAN = "producerExecutor";
    public static final String SPRING_CONTEXT_BEAN = "springContext";

    public static final String PRODUCER_CONTAINER = "producerContainer";

    public static final String CONSUMER_SYNC_EXECUTOR_BEAN = "consumerSyncExecutor";

    public static final String CONSUMER_RETRY_EXECUTOR_BEAN = "consumerRetryExecutor";

    // -------------------------------- phase -------------------------------------------

    public static final int TRANSACTION_MESSAGE_PHASE = 0;

    public static final int SUBSCRIBE_PHASE = TRANSACTION_MESSAGE_PHASE + 10;

    public static final int LISTENER_CONTAINER_PHASE = SUBSCRIBE_PHASE + 10;

    public static final String SUBSCRIBE_TOPIC = "consumer-subscribe";

    public static final String DB_SERVICE_EXECUTOR_NAME = "dbServiceExecutor";

    public static final String KAFKA_BACKUP_MESSAGES = "KAFKA_BACKUP_MESSAGES_V2";

    public static final long BACKUP_MESSAGES_TIMEOUT = 5;
    public static final String MESSAGE_SHARDING_KEY = "created_at";
    public static final String MESSAGE_RECORD_SHARDING_KEY = "poll_time";
    public static final String DELAY_MESSAGE_SHARDING_KEY = "expire_time";

    // -------------------------------- consumer -------------------------------------------

    public static final String KAFKA_LISTENER_CONTAINER_FACTORY = "KafkaListenerContainerFactory";

    // -------------------------------- properties -------------------------------------------

    public static final String APPLICATION_NAME = "spring.application.name";

    // ------------------------------ Redis -----------------------------------------

    public static final String PAUSE_LIST = "courier:consumer:host:pause:%s";
    public static final String STOPPED_LIST = "courier:consumer:host:stopped:%s";
    public static final String STARTED_LIST = "courier:consumer:host:started:%s";

    public static final String HOLDER = "HOLDER";

    public static final String DELAYMESSAGE_MASTERNODE_RACESIGN = "courier:delaymessage:masternode:racesign";

    public static final String DELAYMESSAGE_TIMEWHEEL_POINTER = "courier:delaymessage:timewheel:pointer:%s";

    public static final String DELAYMESSAGE_TIMEWHEEL_STARTTIME = "courier:delaymessage:timewheel:starttime";

    public static final String DELAYMESSAGE_TIMEWHEEL_ENDTIME = "courier:delaymessage:timewheel:endTime";

    public static final String DELAYMESSAGE_TIMEWHEEL_LASTSKIPPOINT = "courier:delaymessage:timewheel:lastskippoint";
}
