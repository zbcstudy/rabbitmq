package com.wondertek.study.rabbitmq;

import com.wondertek.study.rabbitmq.springboot.entity.Order;
import com.wondertek.study.rabbitmq.springboot.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootApplicationTests {

    @Autowired
    Producer producer;

    @Test
    public void testSendMessage() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("num", "1");
        String message = "send spring boot message";
        producer.send(message, headers);
    }

    @Test
    public void testSendOrderMessage() {

        Order order = new Order("2", "name2", "content2");
        producer.sendOrder(order);
    }
}
