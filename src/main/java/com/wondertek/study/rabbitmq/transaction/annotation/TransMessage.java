package com.wondertek.study.rabbitmq.transaction.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TransMessage {
    String exchange() default "";   //要发送的交换机
    String bindingKey() default "";    //要发送的key
    String bizName() default "";      //业务编号
    String dbCoordinator() default "";	//消息落库的处理方式db or redis
}
