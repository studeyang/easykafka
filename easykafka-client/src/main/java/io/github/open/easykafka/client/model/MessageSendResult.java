package io.github.open.easykafka.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 发送消息的结果
 * @author studeyang
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class MessageSendResult {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 原因
     */
    private String reason;

    /**
     * 成功/失败
     */
    private boolean success;

    public static MessageSendResult success(String id) {
        return success(id, null);
    }

    public static MessageSendResult success(String id, String reason) {
        return new MessageSendResult(id, reason, true);
    }

    public static MessageSendResult error(String id, String reason) {
        return new MessageSendResult(id, reason, false);
    }

}
