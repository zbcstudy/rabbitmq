package com.wondertek.study.rabbitmq.quickstart.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.wondertek.study.rabbitmq.quickstart.Constance;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) {
        try {
            Connection connection = Constance.getConnection();

            Channel channel = connection.createChannel();

            //指定消息的确认模式
            channel.confirmSelect();
            String exchangeName = "test_confirm_exchange";
            String rountingKey = "confirm.save";
            String message = "rabbitmq send confirm message";
            channel.basicPublish(exchangeName, rountingKey, null, message.getBytes());
            //添加一个确认监听
            channel.addConfirmListener(new ConfirmListener() {
                //成功
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("-------------ack-----------");
                }

                //失败
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("------------not ack------------");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
