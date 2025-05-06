package io.github.open.easykafka.client.exception;

import io.github.open.easykafka.client.model.ErrorCode;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/16
 */
public class InitializeException extends EasyKafkaException {

    public InitializeException(ErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getDefineCode(), errorCode.getChnDesc());
    }

}
