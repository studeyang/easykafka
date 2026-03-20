package io.github.open.easykafka.client.producer.callback;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/7/1
 */
@FunctionalInterface
public interface SuccessCallback extends MessageCallback {

    /**
     * 默认成功回调
     *
     * @param exception 失败异常
     */
    @Override
    default void onFail(Exception exception) {}

}
