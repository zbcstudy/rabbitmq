package com.wondertek.study.rabbitmq.spring;

import com.wondertek.study.rabbitmq.spring.entity.Order;
import com.wondertek.study.rabbitmq.spring.entity.Packaged;

import java.util.Map;

/**
 * 处理消息的类
 * 方法名是固定的，不能随表改变
 */
public class MessageDelegate {

    /**
     * 默认消息处理方法
     * @param messageBody
     */
    public void handleMessage(byte[] messageBody) {
        System.out.println("默认方法，消息内容：" + new String(messageBody));
    }

    /**
     * 自定义消息处理方法
     * @param messageBody
     */
    public void consumerMessage(byte[] messageBody) {
        System.out.println("字节数组处理方法，消息内容：" + new String(messageBody));
    }

    public void consumerMessage(String messageBody) {
        System.out.println("string类型处理方法，消息内容：" + messageBody);
    }

    public void consumerMethod1(String messageBody) {
        System.out.println("队列与方法名进行绑定，消息内容：" + messageBody);
    }

    public void consumerMessage(Map messageBody) {
        System.out.println("Map类型处理方法，消息内容：" + messageBody);
    }

    public void consumerMessage(Order order) {
        System.out.println("Order类型处理方法，消息内容：" + order);
    }

    public void consumerMessage(Packaged packaged) {
        System.out.println("Packaged类型处理方法，消息内容：" + packaged);
    }
}
