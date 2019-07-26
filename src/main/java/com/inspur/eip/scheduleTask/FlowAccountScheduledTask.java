package com.inspur.eip.scheduleTask;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.FlowAccount2Bss;
import com.inspur.eip.entity.bss.FlowAccountProductList;
import com.inspur.eip.entity.bss.OrderProductItem;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.FlowService;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 流量计费，日志类报文定时任务
 * @Author Zerah
 * @Date 2019/7/24 10:19
 **/
@Component
@Slf4j
//@EnableScheduling
public class FlowAccountScheduledTask {
    @Autowired
    private FlowService flowService;
    @Autowired
    private EipDaoService eipDaoService;
    @Autowired
    private RabbitMessagingTemplate rabbitTemplate;


    @Value("${bss.queues.order.binding.exchange}")
    private String exchange;

    // 发送订单消息的routingKey
    @Value("${bss.queues.order.binding.returnRoutingKey}")
    private String orderKey;

    //每分钟统计
    @Scheduled(cron = "0 0/1 * * * *")
    public void oneMinReportFlowAccount(){
        try {
            List<Eip> trafficEips = eipDaoService.findFlowAccountEipList("Traffic");
            for (Eip eip : trafficEips) {
                Map<String, Long> map = flowService.staticsFlowByPeriod(1, eip.getFloatingIp(), "lasthour", eip.getFirewallId());
                if (map.containsKey(HillStoneConfigConsts.UP_TYPE)){
                    Long up = map.get(HillStoneConfigConsts.UP_TYPE);
                    Long down = map.get(HillStoneConfigConsts.DOWN_TYPE);
                    Long sum = map.get(HillStoneConfigConsts.SUM_TYPE);
                    FlowAccount2Bss flowBean = getFlowAccount2BssBean(eip, up, down, sum);
                    this.sendOrderMessageToBss(flowBean);
                }
            }
        } catch (Exception e) {
            log.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()+":{}",e.getMessage());
        }
    }

    /**
     * 构造给订单的报文
     * @param eip
     * @param up
     * @param down
     * @param sum
     * @return
     */
    public FlowAccount2Bss getFlowAccount2BssBean(Eip eip,Long up, Long down, Long sum){

        FlowAccount2Bss flowBean  = new FlowAccount2Bss();
        flowBean.setSubpackage("flase");
        flowBean.setPackageNo("1");
        flowBean.setBillCycle("1");
        flowBean.setSettleCycle("Day");
        flowBean.setCount("1");
        flowBean.setIndex("1");
        flowBean.setUserId(eip.getUserId());
        flowBean.setProductLineCode("EIP");
        ArrayList<FlowAccountProductList> productLists = new ArrayList<>();

        FlowAccountProductList product = new FlowAccountProductList();
        product.setRegion(eip.getRegion());
        product.setAvailableZone("");
        product.setProductTypeCode("EIP");
        product.setInstanceId(eip.getId());

        List<OrderProductItem> itemList = new ArrayList<>();
        OrderProductItem upItem = new OrderProductItem();
        upItem.setCode(HillStoneConfigConsts.UP_TYPE);
        upItem.setValue(String.valueOf(up));

        OrderProductItem downItem = new OrderProductItem();
        upItem.setCode(HillStoneConfigConsts.DOWN_TYPE);
        upItem.setValue(String.valueOf(down));

        OrderProductItem sumItem = new OrderProductItem();
        upItem.setCode(HillStoneConfigConsts.SUM_TYPE);
        upItem.setValue(String.valueOf(sum));

        itemList.add(upItem);
        itemList.add(downItem);
        itemList.add(sumItem);
        product.setItemList(itemList);

        flowBean.setProductList(productLists);
        return flowBean;
    }

    private void sendOrderMessageToBss(FlowAccount2Bss obj) {
        // 这里会用rabbitMessagingTemplate中配置的MessageConverter自动将obj转换为字节码
        log.info("==========Flow account message to Bss：=======:{}", JSONObject.toJSONString(obj));
        rabbitTemplate.convertAndSend(exchange, orderKey, obj);
    }


}
