package com.wondertek.study.rabbitmq.quickstart.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer4TopicExchange {
    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();
            Channel channel = connection.createChannel();

            //声明
            String exechangeName = "test_topic_exechange";
            String rountingKey1 = "user.save";
            String rountingKey2 = "user.update";
            String rountingKey3 = "user.delete.id";

            String message = "HelloWorld RabbitMq 4 direct exechange";
            channel.basicPublish(exechangeName, rountingKey1, null, (message + rountingKey1).getBytes());
            channel.basicPublish(exechangeName, rountingKey2, null, (message + rountingKey2).getBytes());
            channel.basicPublish(exechangeName, rountingKey3, null, (message + rountingKey3).getBytes());
            Constance.closeSource(channel,connection);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
