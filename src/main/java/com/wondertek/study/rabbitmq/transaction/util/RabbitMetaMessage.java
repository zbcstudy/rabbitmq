package com.wondertek.study.rabbitmq.transaction.util;

/**
 * 消息中间价信息
 * @Author zbc
 * @Date 22:04-2019/2/25
 */
public class RabbitMetaMessage {
    String messageId;
    String exchange;
    String routingKey;
    Object payload;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
