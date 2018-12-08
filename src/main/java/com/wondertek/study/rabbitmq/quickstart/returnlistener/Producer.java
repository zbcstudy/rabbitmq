package com.wondertek.study.rabbitmq.quickstart.returnlistener;

import com.rabbitmq.client.*;
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
            String exchangeName = "test_return_exchange";
            String rountingKey = "return.save";
            String rountingKeyError = "abs.save";
            String message = "rabbitmq send return message";
            //添加一个失败路由监听
            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("--------handle return----------");
                    System.out.println("replyCode:" + replyCode);
                    System.out.println("replyText:" + replyText);
                    System.out.println("exchange:" + exchange);
                    System.out.println("routingKey:" + routingKey);
                    System.out.println("properties:" + properties);
                    System.out.println("body:" + new String(body));
                }
            });
//            channel.basicPublish(exchangeName, rountingKey, true,null, message.getBytes());
            channel.basicPublish(exchangeName, rountingKeyError, true,null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
