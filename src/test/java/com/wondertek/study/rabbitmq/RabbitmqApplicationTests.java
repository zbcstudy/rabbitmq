package com.wondertek.study.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondertek.study.rabbitmq.spring.entity.Order;
import com.wondertek.study.rabbitmq.spring.entity.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqApplicationTests {

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	public void testAdmin() {
		rabbitAdmin.declareExchange(new DirectExchange("test.exchange", false, false));

		rabbitAdmin.declareQueue(new Queue("test.direct.queue"));

		rabbitAdmin.declareBinding(new Binding("test.direct.queue",
				Binding.DestinationType.QUEUE,"test.exchange","test.routingKey",new HashMap<>()));
	}

	/**
	 * 测试消息发送
	 */
	@Test
	public void testSendMessage() {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.getHeaders().put("desc", "信息描述");
		messageProperties.getHeaders().put("type", "自定义消息类型");
		Message message = new Message("hello rabbitmq".getBytes(), messageProperties);
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.out.println("-----------添加额外的设置");
				message.getMessageProperties().getHeaders().put("desc", "额外的信息描述");
				return message;
			}
		});
	}

	/**
	 * 测试messageconverter
	 */
	@Test
	public void testSendMessage4Text() {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("hello rabbitmq".getBytes(), messageProperties);
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", message);
	}

	@Test
	public void testSendJsonMessage() {
		Order order = getorder();

		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		try {
			json = objectMapper.writeValueAsString(order);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("order 4 json: " + json);

		MessageProperties properties = new MessageProperties();
		//设置contentType
		properties.setContentType("application/json");
		Message message = new Message(json.getBytes(), properties);
		rabbitTemplate.convertAndSend("topic001", "spring.order", message);
	}

	@Test
	public void testSendJsonMapper() {
		Order order = getorder();
		ObjectMapper objectMapper = new ObjectMapper();
		String json = "";
		try {
			json = objectMapper.writeValueAsString(order);
			System.out.println("order 4 json:" + json);

			MessageProperties properties = new MessageProperties();
			properties.setContentType("application/json");
			properties.setHeader("__TypeId__", "com.wondertek.study.rabbitmq.spring.entity.Order");
			Message message = new Message(json.getBytes(), properties);
			rabbitTemplate.convertAndSend("topic001", "spring.order", message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试多Java类型映射
	 */
	@Test
	public void testSendMessageMapper() {
		Order order = getorder();
		Packaged packaged = getpackaged();
		ObjectMapper objectMapper = new ObjectMapper();
		String orderJson = "";
		String packagedJson = "";
		try {
			orderJson = objectMapper.writeValueAsString(order);
			System.out.println("orderJson: " + orderJson);
			packagedJson = objectMapper.writeValueAsString(packaged);
			System.out.println("packagedJson: " + packagedJson);

			MessageProperties messageProperties1 = new MessageProperties();
			messageProperties1.setContentType("application/json");
			messageProperties1.getHeaders().put("__TypeId__", "com.wondertek.study.rabbitmq.spring.entity.Order");
			Message message1 = new Message(orderJson.getBytes(), messageProperties1);

			MessageProperties messageProperties2 = new MessageProperties();
			messageProperties2.setContentType("application/json");
			messageProperties1.getHeaders().put("__TypeId__", "com.wondertek.study.rabbitmq.spring.entity.Packaged");
			Message message2 = new Message(packagedJson.getBytes(), messageProperties2);

			rabbitTemplate.convertAndSend("topic001", "spring.order", message1);
			rabbitTemplate.convertAndSend("topic001","spring.packaged",message2);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试发送图片或PDF格式的消息
	 */
	@Test
	public void testSendExtMessageConverter() {

		//测试图片
		/*try {
			byte[] bytes = Files.readAllBytes(Paths.get("D:\\11\\yuanshi\\4.0大纲1.jpg"));
			MessageProperties properties = new MessageProperties();
			properties.setContentType("image/png");
			properties.getHeaders().put("extName", "jpg");
			Message message = new Message(bytes, properties);
			rabbitTemplate.convertAndSend("topic001", "spring.image", message);

		} catch (IOException e) {
			e.printStackTrace();
		}*/

		//测试PDF
		try {
			byte[] bytes = Files.readAllBytes(Paths.get("D:\\11\\yuanshi\\usingthymeleaf.pdf"));
			MessageProperties properties = new MessageProperties();
			properties.setContentType("application/pdf");
			properties.getHeaders().put("extName", "jpg");
			Message message = new Message(bytes, properties);
			rabbitTemplate.convertAndSend("topic001", "spring.pdf", message);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Order getorder() {
		Order order = new Order();
		order.setId("1");
		order.setName("name1");
		order.setContent("content1");
		return order;
	}

	public Packaged getpackaged() {
		Packaged packaged = new Packaged();
		packaged.setId("2");
		packaged.setName("name2");
		packaged.setDescription("description2");
		return packaged;
	}

	@Test
	public void contextLoads() {
	}

}
