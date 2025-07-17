package io.github.open.easykafka.client.producer.callback;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/7/1
 */
public interface MessageCallback {

    /**
     * 消息发送失败
     *
     * @param exception 失败异常
     */
    void onFail(Exception exception);

    /**
     * 消息发送成功
     */
    void onSuccess();

}
