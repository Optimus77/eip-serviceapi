package com.inspur.eip.config.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 配置RabbitMessagingTemplate
 * @Author muning
 * @Date 2019/5/27 15:40
 **/
@Configuration
public class RabbitMqConfig {
    // 配置rabbitMessagingTemplate
    @Bean
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
        // 添加Jackson2JsonMessageConverter，可自动将对象序列化成JSON字符串,如果不配置，需要自行序列化消息进行发送
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return new RabbitMessagingTemplate(rabbitTemplate);
    }

}
