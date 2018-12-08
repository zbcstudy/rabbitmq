package com.wondertek.study.rabbitmq.quickstart.returnlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            String exchangeName = "test_return_exchange";
            String exchangType = "topic";
            String rountingKey = "return.*";
            String queueName = "test_return_queue";

            //声明exchange
            channel.exchangeDeclare(exchangeName, exchangType, true);
            //声明queue
            channel.queueDeclare(queueName, true, false, false, null);
            //消息队列与交换机进行绑定
            channel.queueBind(queueName, exchangeName, rountingKey);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);

            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                System.out.println("获取消息：" + new String(delivery.getBody()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
