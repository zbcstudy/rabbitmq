package com.wondertek.study.rabbitmq.springboot;

import com.wondertek.study.rabbitmq.springboot.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Producer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("correlationData: " + correlationData);
            System.out.println("ack: " + ack);
            System.out.println("cause: " + cause);

        }
    };
    RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("replyCode:" + replyCode + ",replyText: " + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
        }
    };

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setConfirmCallback(confirmCallback);

        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.convertAndSend("topic001", "spring.boot", msg);

    }

    /**
     * 发送一个Java对象
     * @param order
     */
    public void sendOrder(Order order) {
        rabbitTemplate.setConfirmCallback(confirmCallback);

        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData correlationData = new CorrelationData("1234567890");
        rabbitTemplate.convertAndSend("topic002", "springboot.order", order, correlationData);

    }

}
