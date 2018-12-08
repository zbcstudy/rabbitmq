package com.wondertek.study.rabbitmq.quickstart.selfconsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 设置消费限流
 */
public class ConsumerLimit {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            String exchangeName = "test_consumer_exchange";
            String exchangType = "topic";
            String rountingKey = "consumer.*";
            String queueName = "test_consumer_queue";

            //声明exchange
            channel.exchangeDeclare(exchangeName, exchangType, true);
            //声明queue
            channel.queueDeclare(queueName, true, false, false, null);
            //消息队列与交换机进行绑定
            channel.queueBind(queueName, exchangeName, rountingKey);

            /**
             * 设置消息限流
             * prefetchCount:每次接受消息的数量；global：自动签收应用的范围，true-channel级别，false-consumer级别
             * 1：每次接受一条消息
             */
            channel.basicQos(0,3,false);
            //关闭自动签收机制，使用手动签收
            channel.basicConsume(queueName, false, new MyConsumer(channel));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
