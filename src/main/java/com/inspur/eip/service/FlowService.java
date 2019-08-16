package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.bss.FlowAccount2Bss;
import com.inspur.eip.entity.bss.FlowAccountProductList;
import com.inspur.eip.entity.bss.OrderProduct;
import com.inspur.eip.entity.bss.OrderProductItem;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 流量计费实现类
 * @Author Zerah
 * @Date 2019/7/19 10:34
 **/
@Slf4j
@Service
public class FlowService {

    public static final String SPACE_REGEX = "\\s+";

    @Autowired
    private FirewallService firewallService;

    @Autowired
    private EipDaoService eipDaoService;

    @Autowired
    private RabbitMessagingTemplate rabbitTemplate;

    @Value("${bss.queues.order.binding.exchange}")
    private String exchange;

    // 发送订单消息的routingKey
    @Value("${bss.queues.order.binding.returnRoutingKey}")
    private String orderKey;


    /**
     * @param lineNum    需要统计的分钟数，最大支持1小时，即60
     * @param entryName  addressbook name
     * @param period     周期，lasthour
     * @param firewallId 防火墙id
     * @return
     */
    public Map<String, Long> staticsFlowByPeriod(int lineNum, String entryName, String period, String firewallId) {

        ConcurrentHashMap<String, Long> flowMap = new ConcurrentHashMap<>(3);
        if (StringUtils.isBlank(entryName) || StringUtils.isBlank(period)) {
            log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + "entryName:{},period:{}", entryName, period);
            throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
        }
// flowStr not null
        JSONObject json = firewallService.cmdShowStatisticsByAddressBook(entryName, period, firewallId);
        String[] upArrs = json.getString(HillStoneConfigConsts.UP_TYPE).replace("UP  :", " ").trim().split(SPACE_REGEX);
        String[] downArrs = json.getString(HillStoneConfigConsts.DOWN_TYPE).replace("DOWN:", " ").trim().split(SPACE_REGEX);

        int[] upInt = new int[upArrs.length];
        int[] downInt = new int[downArrs.length];
        if (upInt.length == downInt.length) {
            for (int i = 0; i < upInt.length; i++) {
                upInt[i] = Integer.parseInt(upArrs[i]);
                downInt[i] = Integer.parseInt(downArrs[i]);
            }
        }
        long upFlow = 0;
        long downFlow = 0;
//  1：每分钟统计  5：每五分钟统计  0:统计所有
        if (lineNum <= 60 && lineNum >= 1) {
            for (int i = 0; i < lineNum * 2; i++) {
                upFlow = upFlow + upInt[i];
                downFlow = downFlow + downInt[i];
            }

            flowMap.put(HillStoneConfigConsts.UP_TYPE, upFlow);
            flowMap.put(HillStoneConfigConsts.DOWN_TYPE, downFlow);
            flowMap.put(HillStoneConfigConsts.SUM_TYPE, upFlow + downFlow);
        } else {
            log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage() + "lineNum:{}", lineNum);
            throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
        return flowMap;
    }

    /**
     * 释放前统计流量数据
     *
     * @param minute
     */
    public void releaseReportFlowAccount(int minute, Eip eip) {
        try {
            Map<String, Long> map = this.staticsFlowByPeriod(minute, eip.getEipAddress(), "lasthour", eip.getFirewallId());
            if (map.containsKey(HillStoneConfigConsts.UP_TYPE)) {
                Long up = map.get(HillStoneConfigConsts.UP_TYPE);
                Long down = map.get(HillStoneConfigConsts.DOWN_TYPE);
                Long sum = map.get(HillStoneConfigConsts.SUM_TYPE);
                FlowAccount2Bss flowBean = getFlowAccount2BssBean(eip, up, down, sum);

                this.sendOrderMessageToBss(flowBean);
            }
        } catch (Exception e) {
            log.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage() + ":{}", e.getMessage());
        }
    }

    /**
     * 构造给订单的报文
     *
     * @param eip
     * @param up
     * @param down
     * @param sum
     * @return
     */
    public FlowAccount2Bss getFlowAccount2BssBean(Eip eip, Long up, Long down, Long sum) {

        FlowAccount2Bss flowBean = new FlowAccount2Bss();
        flowBean.setSubpackage("flase");
        flowBean.setPackageNo("1");
        flowBean.setBillCycle("1");
        flowBean.setSettleCycle("H");
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

        OrderProductItem bandwidth = new OrderProductItem();
        bandwidth.setCode(HsConstants.BANDWIDTH);
        bandwidth.setValue(String.valueOf(eip.getBandWidth()));
        //暂时只传上行流量
        OrderProductItem upItem = new OrderProductItem();
        upItem.setCode(HillStoneConfigConsts.UP_TYPE);
        upItem.setValue(String.valueOf(up));

        OrderProductItem provider = new OrderProductItem();
        provider.setCode(HsConstants.PROVIDER);
        provider.setValue(eip.getIpType());

        OrderProductItem ip = new OrderProductItem();
        ip.setCode(HsConstants.IP);
        ip.setValue("1");

        OrderProductItem isSbw = new OrderProductItem();
        isSbw.setCode(HsConstants.IS_SBW);
        isSbw.setValue("no");

//        OrderProductItem downItem = new OrderProductItem();
//        upItem.setCode(HillStoneConfigConsts.DOWN_TYPE);
//        upItem.setValue(String.valueOf(down));


        itemList.add(bandwidth);
        itemList.add(upItem);
        itemList.add(provider);
        itemList.add(ip);
        itemList.add(isSbw);
//        itemList.add(downItem);
        product.setItemList(itemList);

        flowBean.setProductList(productLists);
        return flowBean;
    }

    public void sendOrderMessageToBss(FlowAccount2Bss obj) {
        // 这里会用rabbitMessagingTemplate中配置的MessageConverter自动将obj转换为字节码
        log.info("==========Flow account message to Bss：=======:{}", JSONObject.toJSONString(obj));
        rabbitTemplate.convertAndSend(exchange, orderKey, obj);
    }

}
