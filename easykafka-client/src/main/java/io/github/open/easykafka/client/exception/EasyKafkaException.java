package io.github.open.easykafka.client.exception;

/**
 * 消息异常的基类
 *
 * @author studeyang
 */
public class EasyKafkaException extends RuntimeException {

    protected final String status;
    protected final String defineCode;
    protected final String chnDesc;

    public EasyKafkaException(String status, String defineCode, String chnDesc) {
        super("错误代码=" + status + ";错误描述=" + defineCode + "|" + chnDesc);
        this.status = status;
        this.defineCode = defineCode;
        this.chnDesc = chnDesc;
    }

}
