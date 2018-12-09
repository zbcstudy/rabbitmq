package com.wondertek.study.rabbitmq.spring.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * PDF格式转换器
 */
public class PDFMessageConverter implements MessageConverter{
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        System.out.println("---------PDF MessageConverter----------");

        byte[] body = message.getBody();

        String fileName = UUID.randomUUID().toString().replace("-", "");
        String path = "D:/11/" + fileName + ".pdf";

        File file = new File(path);

        try {
            Files.copy(new ByteArrayInputStream(body), file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file;
    }
}
