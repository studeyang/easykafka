package io.github.open.easykafka.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/17
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** ========== Initialize Exception: status 定义为: EK01__  ========== */
    UNDEFINE("EK0101", "UNDEFINE", "未定义"),


    /** ========== Producer Exception: status 定义为: EK02__ ========== */
    PRODUCER_ERROR("EK0201", "PRODUCER_ERROR", "消息发送失败"),
    MESSAGE_MUST_NOTNULL("EK0202", "MESSAGE_MUST_NOTNULL", "推送的消息不能为空"),
    NOT_TOPIC("EK0203", "NOT_TOPIC", "找不到topic"),
    UNRECOGNIZED_USAGE("EK0203", "UNRECOGNIZED_USAGE", "无法识别的Usage"),
    PRODUCER_NOT_FOUND("EK0204", "PRODUCER_NOT_FOUND", "找不到对应的Producer"),
    ;


    private final String status;
    private final String defineCode;
    private final String chnDesc;
}
