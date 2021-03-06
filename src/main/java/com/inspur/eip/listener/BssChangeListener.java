package com.inspur.eip.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspur.eip.entity.bss.OrderSoftDown;
import com.inspur.eip.entity.bss.SoftDownInstance;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.service.impl.RabbitMqServiceImpl;
import com.inspur.eip.util.constant.ConstantClassField;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

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
                        value = @Queue(name = "${bss.queues.change.name.eip}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.change.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.change.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.change.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.change.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.change.binding.routingKey.eip}"
                ),
                @QueueBinding(
                        value = @Queue(name = "${bss.queues.change.name.sbw}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.change.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.change.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.change.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.change.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.change.binding.routingKey.sbw}"
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
    public void process(@Payload Message message, Channel channel)  {

        //允许使用未带引号的字段名
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //允许使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 可以通过message.getBody()获取消息的字节码，并通过ObjectMapper转换成对象
//        log.info(objectMapper.readValue(message.getBody(), Object.class).toString());
        try {
            OrderSoftDown softDown = objectMapper.readValue(message.getBody(), OrderSoftDown.class);
            Optional<OrderSoftDown> optional = Optional.ofNullable(softDown);
            if (optional.isPresent()) {
                SoftDownInstance instance = softDown.getInstanceList().get(0);
                if (HsConstants.EIP.equalsIgnoreCase(instance.getProductLineCode())) {
                    rabbitMqService.softDowOrDeleteEip(softDown);
                } else if (HsConstants.SBW.equalsIgnoreCase(instance.getProductLineCode())) {
                    rabbitMqService.softDowOrDeleteSbw(softDown);
                }else {
                    log.error(ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getMessage(),softDown);
                    throw new EipBadRequestException(ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getCode(),ErrorStatus.NOT_SUPPORT_PRODUCT_LINE_CODE.getMessage());
                }
            }else {
                log.warn(ConstantClassField.PARSE_JSON_PARAM_ERROR,message);
                throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
            }
        } catch (JsonParseException | JsonMappingException e) {
            log.error(ConstantClassField.PARSE_JSON_PARAM_ERROR, message);
            throw new EipInternalServerException(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage());
        } catch (Exception e) {
            log.error(ConstantClassField.PARSE_JSON_IO_ERROR, message);
            throw new EipInternalServerException(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getCode(), ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage());
        }
        // 若配置spring.rabbitmq.listener.simple.default-requeue-rejected=false，当消息处理异常，消息会被转发至死信队列，避免消息阻塞。
        // throw new RuntimeException("123");
        // TODO: 消息处理逻辑
    }
}
