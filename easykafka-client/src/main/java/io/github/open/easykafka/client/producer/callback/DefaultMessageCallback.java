package io.github.open.easykafka.client.producer.callback;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/7/1
 */
public class DefaultMessageCallback implements MessageCallback {

    @Override
    public void onFail(Exception exception) {
        // do nothing
    }

    @Override
    public void onSuccess() {
        // do nothing
    }
}
