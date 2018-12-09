package com.wondertek.study.rabbitmq.spring;

import com.rabbitmq.client.test.ssl.ConnectionFactoryDefaultTlsVersion;
import com.wondertek.study.rabbitmq.spring.converter.ImageMessageConverter;
import com.wondertek.study.rabbitmq.spring.converter.PDFMessageConverter;
import com.wondertek.study.rabbitmq.spring.converter.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@ComponentScan(basePackages = {"com.wondertek.study.rabbitmq.spring"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses("192.168.115.128:5672");
        connectionFactory.setHost(Constance.DEFAULT_HOST);
        connectionFactory.setPort(Constance.DEFAULT_PORT);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost(Constance.DEFAULT_VIRTUALHOST);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001()); //设置监听队列，可以同时监听多个队列
        container.setConcurrentConsumers(1); //设置消费者数量
        container.setMaxConcurrentConsumers(5); //设置最大消费者数量
        container.setDefaultRequeueRejected(false); //设置消息是否回退到队列中
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); //设置签收模式：自动签收
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });
       /* //设置监听
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody());
                System.out.println("----------消费者获取消息：" + msg);
            }
        });*/
        //设置一个消息监听适配器

        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        //设置消息监听适配器的消息处理方法，默认处理方法是handleMessage
        adapter.setDefaultListenerMethod("consumerMessage");
        //设置消息转换
        adapter.setMessageConverter(new TextMessageConverter());
        */

        /*
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setMessageConverter(new TextMessageConverter());

        Map<String, String> queueOrTagToMethodName = new HashMap<>();
        //队列名称与方法名称进行一一对应
        queueOrTagToMethodName.put("queue001", "consumerMethod1");
        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
        container.setMessageListener(adapter);
        */


        //1.1json格式的装换器
        /*MessageListenerAdapter adapter1 = new MessageListenerAdapter(new MessageDelegate());
        adapter1.setDefaultListenerMethod("consumerMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        adapter1.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter1);*/

        //1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持Java对象装换
       /* MessageListenerAdapter adapter2 = new MessageListenerAdapter(new MessageDelegate());
        adapter2.setDefaultListenerMethod("consumerMessage");
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
        messageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
        adapter2.setMessageConverter(messageConverter);
        container.setMessageListener(adapter2);*/


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持Java对象多映射装换
        //使用此方式用问题，暂时未解决
        /*MessageListenerAdapter adapter3 = new MessageListenerAdapter(new MessageDelegate());
        adapter3.setDefaultListenerMethod("consumerMessage");
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("order", com.wondertek.study.rabbitmq.spring.entity.Order.class);
        idClassMapping.put("packaged", com.wondertek.study.rabbitmq.spring.entity.Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMapping);
        jsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        container.setMessageListener(adapter3);*/

        //1.4 ext converter
        MessageListenerAdapter adapter4 = new MessageListenerAdapter(new MessageDelegate());
        //设置适配器默认的监听方法
        adapter4.setDefaultListenerMethod("consumerMessage");
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textMessageConverter = new TextMessageConverter();
        converter.addDelegate("text", textMessageConverter);
        converter.addDelegate("html/text", textMessageConverter);
        converter.addDelegate("xml/text", textMessageConverter);
        converter.addDelegate("text/plain", textMessageConverter);

        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        converter.addDelegate("json", jsonMessageConverter);
        converter.addDelegate("application/json", jsonMessageConverter);

        ImageMessageConverter imageMessageConverter = new ImageMessageConverter();
        converter.addDelegate("image/png", imageMessageConverter);
        converter.addDelegate("image", imageMessageConverter);

        PDFMessageConverter pdfMessageConverter = new PDFMessageConverter();
        converter.addDelegate("application/pdf", pdfMessageConverter);
        adapter4.setMessageConverter(converter);
        container.setMessageListener(adapter4);
        return container;
    }

    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true);
    }

    @Bean
    public Binding bind001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.#");
    }
}
