package io.github.open.easykafka.client.exception;

/**
 * 消息异常的基类
 *
 * @author 005964
 */
public class EasyKafkaException extends RuntimeException {

    protected final String status;
    protected final String defineCode;
    protected final String chnDesc;

    public EasyKafkaException(String status, String defineCode, String chnDesc) {
        super(status + "(" + defineCode + ") | " + chnDesc);
        this.status = status;
        this.defineCode = defineCode;
        this.chnDesc = chnDesc;
    }

}
