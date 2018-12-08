package com.wondertek.study.rabbitmq.quickstart.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            String exchangeName = "test_dlx_exchange";
            String exchangType = "topic";
            String rountingKey = "dlx.*";
            String queueName = "test_dlx_queue";

            //声明exchange
            channel.exchangeDeclare(exchangeName, exchangType, true,false,null);

            //设置arguments参数：死信队列
            Map<String, Object> arguments = new HashMap<>();
            //map中的value值指定的是死信队列的exchange
            arguments.put("x-dead-letter-exchange", "dlx.exchange");
            //声明queue,死信队列的参数设置应该设置在queueDeclare中，不能放在交换机声明中
            channel.queueDeclare(queueName, true, false, false, arguments);
            //消息队列与交换机进行绑定
            channel.queueBind(queueName, exchangeName, rountingKey);

            //声明一个私信队列
            channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
            channel.queueDeclare("dlx.queue", true, false, false, null);
            //队列路由所有的消息
            channel.queueBind("dlx.queue", "dlx.exchange", "#");

            channel.basicConsume(queueName, true, new MyConsumer(channel));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
