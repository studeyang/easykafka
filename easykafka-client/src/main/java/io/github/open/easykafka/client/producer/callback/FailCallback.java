package io.github.open.easykafka.client.producer.callback;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/7/1
 */
@FunctionalInterface
public interface FailCallback extends MessageCallback {

    /**
     * 失败回调
     */
    @Override
    default void onSuccess() {}

}
