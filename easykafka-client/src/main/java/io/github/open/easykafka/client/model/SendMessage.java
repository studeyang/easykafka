package io.github.open.easykafka.client.model;

import io.github.open.easykafka.client.message.Usage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 发送消息
 *
 * @author studeyang
 */
@Data
@Accessors(chain = true)
public class SendMessage {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息的topic
     */
    private TopicMetadata topic;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息发送方的service
     */
    private String service;

    /**
     * 发往kafka的消息体，String类型的json字符串
     */
    private String content;

    /**
     * 消息的创建时间
     */
    private Date createdAt;

    /**
     * 用途
     */
    private Usage usage;

}
