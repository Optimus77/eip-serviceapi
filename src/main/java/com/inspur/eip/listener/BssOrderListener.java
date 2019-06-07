package com.inspur.eip.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspur.eip.entity.ReciveOrder;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.service.impl.RabbitMqServiceImpl;
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
 * @Description 订单接口 监听器实现
 * @Author muning
 * @Date 2019/5/27 15:34
 **/
@Component
// 可以通过注解完成队列的绑定，如果队列不存在，则会自动新建
//目前在队列上配置了x-message-ttl参数，当消息超过x-message-ttl定义的时间仍然未被消费，则会自动转发至死信队列，以防业务队列阻塞。
@RabbitListener(
        bindings = {
                @QueueBinding(
                        value = @Queue(name = "${bss.queues.order.name.eip}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.order.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.order.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.order.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.order.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.order.binding.routingKey.eip}"
                ),
                @QueueBinding(
                        value = @Queue(name = "${bss.queues.order.name.sbw}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.order.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.order.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.order.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.order.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.order.binding.routingKey.sbw}"
                ),
                @QueueBinding(
                        value = @Queue(name = "${bss.queues.order.name.ipts}", arguments = {
                                @Argument(name = "x-message-ttl", value = "${bss.queues.order.messageTTL}", type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = "${bss.queues.order.deadLetterExchange}"),
                                @Argument(name = "x-dead-letter-routing-key", value = "${bss.queues.order.deadLetterRoutingKey}")
                        }),
                        exchange = @Exchange(name = "${bss.queues.order.binding.exchange}", type = ExchangeTypes.TOPIC),
                        key = "${bss.queues.order.binding.routingKey.ipts}"
                )
        }
)
@Slf4j
public class BssOrderListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitMqServiceImpl rabbitMqService;

    // 必须配置一个handler为默认handler，避免消息在未配置Content-Type头时无法被处理
    @RabbitHandler(isDefault = true)
    public void process(@Payload Message message, Channel channel) throws  IOException {

        //允许使用未带引号的字段名
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //允许使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 可以通过message.getBody()获取消息的字节码，并通过ObjectMapper转换成对象
//        log.info(objectMapper.readValue(message.getBody(), Object.class).toString());
        try {
            ReciveOrder reciveOrder = objectMapper.readValue(message.getBody(), ReciveOrder.class);
            Optional<ReciveOrder> optional = Optional.ofNullable(reciveOrder);
            if (optional.isPresent()){
                String orderType = reciveOrder.getOrderType();
                String orderRoute = reciveOrder.getOrderRoute();
                switch (orderType){
                    case HsConstants.NEW_ORDERTYPE:
                        if (HsConstants.EIP.equalsIgnoreCase(orderRoute) || HsConstants.IPTS.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.createEipInfo(reciveOrder);
                        }else if (HsConstants.SBW.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.createSbwInfo(reciveOrder);
                        }
                        break;
                    case HsConstants.CHANGECONFIGURE_ORDERTYPE:
                    case HsConstants.RENEW_ORDERTYPE:
                        if (HsConstants.EIP.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.updateEipInfoConfig(reciveOrder);
                        }else if (HsConstants.SBW.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.updateSbwInfoConfig(reciveOrder);
                        }
                        break;
                    case HsConstants.UNSUBSCRIBE_ORDERTYPE:
                        if (HsConstants.EIP.equalsIgnoreCase(orderRoute)|| HsConstants.IPTS.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.deleteEipConfig(reciveOrder);
                        }else if (HsConstants.SBW.equalsIgnoreCase(orderRoute)){
                            rabbitMqService.deleteSbwConfig(reciveOrder);
                        }
                        break;
                    default:
                        log.error(ErrorStatus.NOT_SUPPORT_ORDER_TYPE.getMessage(),orderType);
                        throw new EipBadRequestException(ErrorStatus.NOT_SUPPORT_ORDER_TYPE.getCode(),ErrorStatus.NOT_SUPPORT_ORDER_TYPE.getMessage());
                }
            }else {
                log.warn(ConstantClassField.PARSE_JSON_PARAM_ERROR);
                throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(),ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
            }
        } catch (JsonParseException |JsonMappingException  e) {
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
