package com.inspur.eip.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspur.eip.entity.OrderSoftDown;
import com.inspur.eip.entity.SoftDownInstance;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.service.RabbitMqServiceImpl;
import com.inspur.eip.util.ConstantClassField;
import com.inspur.eip.util.ErrorStatus;
import com.inspur.eip.util.HsConstants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * @Description 停服队列监听器实现
 * @Author muning
 * @Date 2019/5/28 11:19
 **/
@Component
// 可以通过注解完成队列的绑定，如果队列不存在，则会自动新建
@RabbitListener(
        bindings = {
                @QueueBinding(
                        value = @Queue(name = "${bss.queues.change.name}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.change.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.change.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.change.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.change.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.change.binding.routingKey}"
                )
        }
)
@Slf4j
public class BssChangeListener {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitMqServiceImpl rabbitMqService;

    // 必须配置一个handler为默认handler，避免消息在未配置Content-Type头时无法被处理
    @RabbitHandler(isDefault = true)
    public void process(@Payload Message message, Channel channel) throws JsonParseException, JsonMappingException, IOException {
        // 可以通过message.getBody()获取消息的字节码，并通过ObjectMapper转换成对象
        log.info(objectMapper.readValue(message.getBody(), Object.class).toString());
        try {
            OrderSoftDown softDown = objectMapper.readValue(message.getBody(), OrderSoftDown.class);
            Optional<OrderSoftDown> optional = Optional.ofNullable(softDown);
            if (optional.isPresent()) {
                SoftDownInstance instance = softDown.getInstanceList().get(0);
                if (HsConstants.EIP.equalsIgnoreCase(instance.getProductLineCode())) {
                    rabbitMqService.softDowOrDeleteEip(softDown);
                    return;
                } else if (HsConstants.SBW.equalsIgnoreCase(instance.getProductLineCode())) {
                    rabbitMqService.softDowOrDeleteSbw(softDown);
                    return;
                }else {
                    log.error(ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getMessage(),instance.getProductLineCode());
                    throw new EipBadRequestException(ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getCode(),ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getMessage());
                }
            }else {
                String msg = String.format(ConstantClassField.PARSE_JSON_PARAM_ERROR, message.getBody().toString());
                log.warn(msg);
                throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
            }
        } catch (JsonParseException | JsonMappingException e) {
            String msg = String.format(ConstantClassField.PARSE_JSON_PARAM_ERROR, message.getBody().toString());
            log.error(msg, e);
            throw new EipInternalServerException(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), msg);
        } catch (IOException e) {
            String msg = String.format(ConstantClassField.PARSE_JSON_IO_ERROR, message.getBody().toString());
            log.error(msg, e);
            throw new EipInternalServerException(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), msg);
        }
        // 若配置spring.rabbitmq.listener.simple.default-requeue-rejected=false，当消息处理异常，消息会被转发至死信队列，避免消息阻塞。
        // throw new RuntimeException("123");
        // TODO: 消息处理逻辑
    }
}
