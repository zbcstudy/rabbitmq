package com.wondertek.study.rabbitmq.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wondertek.study.rabbitmq.springboot.entity.Order;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息队列的消费者
 */
@Component
public class Consumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue001",durable = "true"),
            exchange = @Exchange(value = "topic001",
            durable = "true",
            type = "topic",
            ignoreDeclarationExceptions = "true"),
            key = "spring.boot"
    ))
    @RabbitHandler
    public void consumerMessage(Message message, Channel channel) throws IOException {

        System.out.println("--------------消费端---------------");
        Object payload = message.getPayload();
        System.out.println("获取消息；" + payload);
        Long delecery_tag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(delecery_tag, false);
    }

    /**
     * 采用配置文件进行参数注入
     * spring.rabbitmq.listener.order.queue.name=queue001
     * spring.rabbitmq.listener.order.queue.durable=true
     * spring.rabbitmq.listener.order.exchange.name=topic001
     * spring.rabbitmq.listener.order.exchange.type=topic
     * spring.rabbitmq.listener.order.exchange.durable=true
     * spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=true
     * spring.rabbitmq.listener.order.key=spring.boot
     */

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(
                    value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}"
    ))
    @RabbitHandler
    public void consumerOrderMessage(@Payload Order order, Message message, Channel channel,
                                     @Headers Map<String, Object> headers) {
        System.out.println("--------------消费端-------------");
        System.out.println("orderId---" + order.getId());
        Long delevery_tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            channel.basicAck(delevery_tag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
