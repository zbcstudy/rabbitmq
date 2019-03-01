package com.wondertek.study.rabbitmq.transaction.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondertek.study.rabbitmq.transaction.util.CompleteCorrelationData;
import com.wondertek.study.rabbitmq.transaction.util.RabbitMetaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * rabbitmq发送消息
 * @Author zbc
 * @Date 1:21-2019/3/2
 */
@Component
public class RabbitSender {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    //扩展消息的CorrelationData,方便在回调中应用
    public void setCorrelationData(String corrdinator) {
        rabbitTemplate.setCorrelationDataPostProcessor((message,correlationData)->{
            return new CompleteCorrelationData(correlationData != null ? correlationData.getId() : null, corrdinator);
        });
    }

    public String send(RabbitMetaMessage rabbitMetaMessage) {
        final String msgId = UUID.randomUUID().toString();
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setMessageId(msgId);
                //设置消息持久化
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(rabbitMetaMessage.getPayload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        try {
            rabbitTemplate.convertAndSend(rabbitMetaMessage.getExchange(), rabbitMetaMessage.getRoutingKey(), message,
                    messagePostProcessor, new CorrelationData(msgId));

            logger.info("发送消息,消息ID：{}", msgId);
            return msgId;
        } catch (AmqpException e) {
            throw new RuntimeException("rabbitmq发送消息失败"+e);
        }

    }
}
