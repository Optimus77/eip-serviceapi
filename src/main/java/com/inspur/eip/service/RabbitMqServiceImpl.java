package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.*;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.sbw.SbwUpdateParamWrapper;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.inspur.eip.util.CommonUtil.preCheckParam;
import static com.inspur.eip.util.CommonUtil.preSbwCheckParam;

/**
 * @Description convert to mq
 * @Author muning
 * @Date 2019/5/28 10:59
 **/
@Service
@Slf4j
public class RabbitMqServiceImpl {
    @Autowired
    private RabbitMessagingTemplate rabbitTemplate;

    @Autowired
    private SbwAtomService sbwAtomService;

    @Autowired
    private EipAtomService eipAtomService;

    @Autowired
    private WebControllerService webService;


    // 发送消息的exchange
    @Value("${bss.queues.order.binding.exchange}")
    private String exchange;

    // 发送消息的routingKey
    @Value("${bss.queues.order.binding.returnRoutingKey}")
    private String routingKey;

    private void sendMessageToBss(Object obj) {
        // 这里会用rabbitMessagingTemplate中配置的MessageConverter自动将obj转换为字节码
        rabbitTemplate.convertAndSend(exchange, routingKey, obj);
    }

    /**
     *  create an  eip entity info
     * @param eipOrder order
     * @return return message
     */
    public JSONObject createEipInfo(ReciveOrder eipOrder) {

        String code;
        String msg;
        String eipId = "";
        JSONObject createRet = null;
        try {
            log.debug("Recive create order:{}", JSONObject.toJSONString(eipOrder));
            if (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                EipAllocateParam eipConfig = getEipConfigByOrder(eipOrder);
                ReturnMsg checkRet = preCheckParam(eipConfig);
                if (checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                    //post request to atom
                    EipAllocateParamWrapper eipAllocateParamWrapper = new EipAllocateParamWrapper();
                    eipAllocateParamWrapper.setEip(eipConfig);
                    createRet = eipAtomService.atomCreateEip(eipAllocateParamWrapper);
                    String retStr = HsConstants.SUCCESS;

                    if (createRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                        retStr = HsConstants.FAIL;
                        log.info("create eip failed, return code:{}", createRet.getInteger(HsConstants.STATUSCODE));
                    } else {
                        JSONObject eipEntity = createRet.getJSONObject("eip");
                        eipId = eipEntity.getString("eipid");
                        webService.returnsWebsocket(eipEntity.getString("eipid"), eipOrder, "create");
                        if (eipConfig.getIpv6().equalsIgnoreCase("yes")) {
                            webService.returnsIpv6Websocket("Success", "Success", "createNatWithEip");
                        }
                    }
                    sendMessageToBss(getEipOrderResult(eipOrder, eipId, retStr));
                    return createRet;
                } else {
                    code = ReturnStatus.SC_PARAM_ERROR;
                    msg = checkRet.getMessage();
                    log.error(msg);
                }
            } else {
                code = ReturnStatus.SC_RESOURCE_ERROR;
                msg = "not payed.";
                log.info(msg);
            }
        } catch (Exception e) {
            log.error("Exception in createEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * delete result form bss
     * @param eipOrder order
     * @return string
     */
    public JSONObject deleteEipConfig(ReciveOrder eipOrder) {
        String msg;
        String code;
        String eipId = "";
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(eipOrder));
            if (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {

                List<OrderProduct> orderProducts = eipOrder.getProductList();
                for (OrderProduct orderProduct : orderProducts) {
                    eipId = orderProduct.getInstanceId();
                }
                JSONObject delResult = eipAtomService.atomDeleteEip(eipId);

                if (delResult.getInteger(HsConstants.STATUSCODE) == org.springframework.http.HttpStatus.OK.value()) {
                    if (eipOrder.getConsoleCustomization().containsKey("operateType")
                            && eipOrder.getConsoleCustomization().getString("operateType").equalsIgnoreCase("deleteNatWithEip")) {
                        webService.returnsIpv6Websocket("Success", "Success", "deleteNatWithEip");
                    } else {
                        webService.returnsWebsocket(eipId, eipOrder, "delete");
                    }
                    sendMessageToBss(getEipOrderResult(eipOrder,eipId, HsConstants.UNSUBSCRIBE));
                    return delResult;
                } else {
                    msg = delResult.getString(HsConstants.STATUSCODE);
                    code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                }
            } else {
                msg = "Failed to delete eip,failed to create delete. orderStatus: " + eipOrder.getOrderStatus();
                code = ReturnStatus.SC_PARAM_UNKONWERROR;
                log.error(msg);
            }
        } catch (Exception e) {
            log.error("Exception in deleteEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        sendMessageToBss(getEipOrderResult(eipOrder, eipId, HsConstants.FAIL));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * update eip config
     * @param eipId    id
     * @param eipOrder order
     * @return string
     */
    public JSONObject updateEipInfoConfig(String eipId, ReciveOrder eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;

        try {
            log.debug("Recive update order:{}", JSONObject.toJSONString(eipOrder));

            if ((null != eipOrder) && (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS))) {
                EipUpdateParam eipUpdate = getUpdatParmByOrder(eipOrder);
                JSONObject updateRet;
                if (eipOrder.getOrderType().equalsIgnoreCase("changeConfigure")) {
                    updateRet = eipAtomService.atomUpdateEip(eipId, eipUpdate);
                } else if (eipOrder.getOrderType().equalsIgnoreCase("renew") && eipOrder.getBillType().equals(HsConstants.MONTHLY)) {
                    updateRet = eipAtomService.atomRenewEip(eipId, eipUpdate);
                } else {
                    log.error("Not support order type:{}", eipOrder.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                String retStr = HsConstants.SUCCESS;
                if (updateRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                    retStr = HsConstants.FAIL;
                }

                log.debug("renew order result :{}", updateRet);
                webService.returnsWebsocket(eipId, eipOrder, "update");
                sendMessageToBss(getEipOrderResult(eipOrder,eipId, retStr));
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        if (null != eipOrder) {
            sendMessageToBss(getEipOrderResult(eipOrder,eipId, HsConstants.FAIL));
        }
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * soft down order from bss
     * @param eipOrder order
     * @return return
     */
    public JSONObject softDowOrDeleteEip(OrderSoftDown eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        JSONObject updateRet = null;
        String retStr;
        String iStatusStr;
        try {
            log.debug("Recive soft down or delete order:{}", JSONObject.toJSONString(eipOrder));
            List<SoftDownInstance> instanceList = eipOrder.getInstanceList();
            for (SoftDownInstance softDownInstance : instanceList) {
                String operateType = softDownInstance.getOperateType();
                if ("delete".equalsIgnoreCase(operateType)) {
                    updateRet = eipAtomService.atomDeleteEip(softDownInstance.getInstanceId());
                    iStatusStr = HsConstants.DELETED;
                    retStr = HsConstants.SUCCESS;
                    if (updateRet.getInteger(HsConstants.STATUSCODE) !=  HttpStatus.SC_OK){
                        retStr = HsConstants.FAIL;
                        iStatusStr = HsConstants.FAIL;
                    }
                } else if (HsConstants.STOPSERVER.equalsIgnoreCase(operateType)) {
                    EipUpdateParam updateParam = new EipUpdateParam();
                    updateParam.setDuration("0");
                    updateRet = eipAtomService.atomRenewEip(softDownInstance.getInstanceId(), updateParam);
                    if (updateRet.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK){
                        iStatusStr = HsConstants.STOPSERVER;
                        retStr = HsConstants.SUCCESS;
                    }else if(updateRet.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_NOT_FOUND){
                        iStatusStr = HsConstants.NOTFOUND;
                        retStr = HsConstants.SUCCESS;
                    } else {
                        retStr = HsConstants.FAIL;
                        iStatusStr = HsConstants.FAIL;
                    }
                }else if ("resumeServer".equalsIgnoreCase(operateType)){
                    EipUpdateParam eipUpdate = new EipUpdateParam();
                    eipUpdate.setDuration("1");
                    updateRet = eipAtomService.atomRenewEip(softDownInstance.getInstanceId(), eipUpdate);
                    if (updateRet.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK){
                        iStatusStr = HsConstants.SUCCESS;
                        retStr = HsConstants.SUCCESS;
                    }else {
                        iStatusStr = HsConstants.FAIL;
                        retStr = HsConstants.FAIL;
                    }
                }else {
                    continue;
                }

                softDownInstance.setResult(retStr);
                softDownInstance.setInstanceStatus(iStatusStr);
                softDownInstance.setStatusTime(CommonUtil.getDate());
                log.debug("Soft down result:{}", updateRet);
            }
            if (null != updateRet) {
                sendMessageToBss(eipOrder);
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in soft down or delete eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        sendMessageToBss(eipOrder);
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * get create shareband result
     * @return return message
     */
    public JSONObject createSbwInfo(ReciveOrder reciveOrder) {

        String code;
        String msg;
        String sbwId = "";
        JSONObject createRet = null;
        ReturnResult returnResult = null;
        try {
            if (reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParam sbwConfig = getSbwConfigByOrder(reciveOrder);
                ReturnSbwMsg checkRet = preSbwCheckParam(sbwConfig);
                if (checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                    //post request to atom
                    SbwUpdateParamWrapper sbwWrapper = new SbwUpdateParamWrapper();
                    sbwWrapper.setSbw(sbwConfig);
                    createRet = sbwAtomService.atomCreateSbw(sbwWrapper);
                    String retStr = HsConstants.STATUS_ACTIVE;

                    if (createRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                        retStr = HsConstants.STATUS_ERROR;
                        log.error("create sbw failed, return code:{}", createRet.getInteger(HsConstants.STATUSCODE));
                    } else {
                        JSONObject sbwEntity = createRet.getJSONObject("sbw");
                        sbwId = sbwEntity.getString("sbwId");
                        webService.returnSbwWebsocket(sbwEntity.getString("sbwId"), reciveOrder, "create");
                    }
                    sendMessageToBss(packageSbwReturnResult(reciveOrder, sbwId, retStr));
                } else {
                    msg = checkRet.getMessage();
                    log.error(msg);
                }
            } else {
                msg = "not payed.";
                log.info(msg);
            }
        } catch (Exception e) {
            log.error("Exception in createSbw", e);
        }
        return createRet;
    }
    /**
     * delete result from bss
     * @param reciveOrder order
     * @return string
     */
    public JSONObject deleteSbwConfig(ReciveOrder reciveOrder) {
        String msg;
        String code;
        String sbwId = "";
        JSONObject result = new JSONObject();
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(reciveOrder));
            if (reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {

                List<OrderProduct> productList = reciveOrder.getProductList();
                for (OrderProduct product : productList) {
                    sbwId = product.getInstanceId();
                }
                JSONObject delResult = sbwAtomService.atomDeleteSbw(sbwId);

                if (delResult.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK) {
                    //Return message to the front des
                    webService.returnSbwWebsocket(sbwId, reciveOrder, "delete");
                    sendMessageToBss(packageSbwReturnResult(reciveOrder,sbwId, HsConstants.STATUS_DELETE));
                    return delResult;
                } else {
                    msg = delResult.getString(HsConstants.STATUSCODE);
                    code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                }
            } else {
                msg = "Failed to delete SBW,failed to create delete. orderStatus: " + reciveOrder.getOrderStatus();
                code = ReturnStatus.SC_PARAM_UNKONWERROR;
                log.error(msg);
            }
        } catch (Exception e) {
            log.error("Exception in deleteEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
       sendMessageToBss(packageSbwReturnResult(reciveOrder,sbwId, HsConstants.STATUS_ERROR));
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * update the sbw config,incloud bandWidth and eip
     * @param sbwId  id
     * @param recive info recived
     * @return ret
     */
    public JSONObject updateSbwInfoConfig(String sbwId, ReciveOrder recive) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        String retStr = HsConstants.STATUS_ACTIVE;
        try {
            log.info("Update sbw config:{}", JSONObject.toJSONString(recive));
            if (recive.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                SbwUpdateParam sbwUpdate = getSbwConfigByOrder(recive);
                wrapper.setSbw(sbwUpdate);
                JSONObject updateRet;
                if (recive.getOrderType().equalsIgnoreCase("changeConfigure")) {
                    updateRet = sbwAtomService.atomUpdateSbw(sbwId, wrapper);
                } else if (recive.getOrderType().equalsIgnoreCase("renew") && recive.getBillType().equals(HsConstants.MONTHLY)) {
                    updateRet = sbwAtomService.atomRenewSbw(sbwId, wrapper);
                } else {
                    log.error("Not support order type:{}", recive.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                if (updateRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                    retStr = HsConstants.STATUS_ERROR;
                }else {
                    log.info("update order result :{}", updateRet);
                    webService.returnSbwWebsocket(sbwId, recive, "update");
                }
                sendMessageToBss(packageSbwReturnResult(recive,sbwId, retStr));
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in update sbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        sendMessageToBss(packageSbwReturnResult(recive, sbwId, retStr));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * 停服或者删除共享带宽
     * @param softDown
     * @return
     */
    public JSONObject softDowOrDeleteSbw(OrderSoftDown softDown) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        JSONObject updateRet = null;
        String setStatus = HsConstants.SUCCESS;

        String instanceStatusStr = "";
        try {
            log.debug("Recive soft down or delete order:{}", JSONObject.toJSONString(softDown));
            List<SoftDownInstance> instanceList = softDown.getInstanceList();
            for (SoftDownInstance instance : instanceList) {
                String operateType = instance.getOperateType();
                if ("delete".equalsIgnoreCase(operateType)) {
                    updateRet = sbwAtomService.atomDeleteSbw(instance.getInstanceId());
                } else if ("stopServer".equalsIgnoreCase(operateType)) {
                    SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                    SbwUpdateParam updateParam = new SbwUpdateParam();
                    updateParam.setDuration("0");
                    wrapper.setSbw(updateParam);
                    updateRet = sbwAtomService.atomRenewSbw(instance.getInstanceId(), wrapper);
                } else {
                    continue;
                }

                if ("stopServer".equalsIgnoreCase(operateType)) {
                    instanceStatusStr = HsConstants.STATUS_STOP;
                } else if ("delete".equalsIgnoreCase(operateType)) {
                    instanceStatusStr = HsConstants.STATUS_DELETE;
                }

                if (updateRet.getInteger(HsConstants.STATUSCODE) != org.springframework.http.HttpStatus.OK.value()) {
                    setStatus = HsConstants.FAIL;
                    instanceStatusStr = HsConstants.STATUS_ERROR;
                }
                instance.setResult(setStatus);
                instance.setInstanceStatus(instanceStatusStr);
                instance.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                log.info("Soft down or delete result:{}", updateRet);
            }
            if (null != updateRet) {
                sendMessageToBss(softDown);
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in softDowOrDeleteSbw sbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        sendMessageToBss(softDown);
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * extract create eip config from BSS MQ
     * @param eipOrder order
     * @return eip param
     */
    private EipAllocateParam getEipConfigByOrder(ReciveOrder eipOrder) {
        EipAllocateParam eipAllocateParam = new EipAllocateParam();
        List<OrderProduct> orderProducts = eipOrder.getProductList();

        eipAllocateParam.setBillType(eipOrder.getBillType());
        eipAllocateParam.setChargemode(HsConstants.CHARGE_MODE_BANDWIDTH);

        for (OrderProduct orderProduct : orderProducts) {
            if (!orderProduct.getProductLineCode().equals(HsConstants.EIP)) {
                continue;
            }
            eipAllocateParam.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for (OrderProductItem orderProductItem : orderProductItems) {
                if (orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                    eipAllocateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                } else if (orderProductItem.getCode().equals(HsConstants.PROVIDER)) {
                    eipAllocateParam.setIptype(orderProductItem.getValue());
                } else if (orderProductItem.getCode().equals(HsConstants.IS_SBW) &&
                        orderProductItem.getValue().equals(HsConstants.YES)) {
                    eipAllocateParam.setChargemode(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH);
                } else if (orderProductItem.getCode().equals(HsConstants.WITH_IPV6) &&
                        orderProductItem.getValue().equals(HsConstants.YES)) {
                    eipAllocateParam.setIpv6("yes");
                } else if (orderProductItem.getCode().equals(HsConstants.SBW_ID)) {
                    eipAllocateParam.setSbwId(orderProductItem.getValue());
                }
            }
        }
        log.info("Get eip param from order:{}", eipAllocateParam.toString());
        /*chargemode now use the default value */
        return eipAllocateParam;
    }
    /**
     * extract update eip config from BSS MQ
     * @param eipOrder order
     * @return eip param
     */
    private EipUpdateParam getUpdatParmByOrder(ReciveOrder eipOrder) {
        EipUpdateParam eipAllocateParam = new EipUpdateParam();

        List<OrderProduct> orderProducts = eipOrder.getProductList();
        eipAllocateParam.setBillType(eipOrder.getBillType());
        eipAllocateParam.setChargemode(HsConstants.CHARGE_MODE_BANDWIDTH);
        eipAllocateParam.setDuration(eipOrder.getDuration());
        for (OrderProduct orderProduct : orderProducts) {
            if (!orderProduct.getProductLineCode().equals(HsConstants.EIP)) {
                continue;
            }
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for (OrderProductItem orderProductItem : orderProductItems) {
                if (orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                    eipAllocateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                } else if (orderProductItem.getCode().equals(HsConstants.IS_SBW) &&
                        orderProductItem.getValue().equalsIgnoreCase(HsConstants.YES)) {
                    eipAllocateParam.setChargemode(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH);
                } else if (orderProductItem.getCode().equals(HsConstants.SBW_ID)) {
                    eipAllocateParam.setSbwId(orderProductItem.getValue());
                }
            }
        }
        log.info("Get eip param from bss MQ:{}", eipAllocateParam.toString());
        /*chargemode now use the default value */
        return eipAllocateParam;
    }

    /**
     * extract EIP message from entity to return BSS MQ
     * @param reciveOrder order
     * @return EipOrderResult
     */
    private EipOrderResult getEipOrderResult(ReciveOrder reciveOrder,String eipId, String result) {
        //must not be delete ,set the reference
        List<OrderProduct> orderProducts = reciveOrder.getProductList();

        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setInstanceId(eipId);
            orderProduct.setInstanceStatus(result);
            orderProduct.setStatusTime(reciveOrder.getStatusTime());
        }
        EipOrderResult eipOrderResult = new EipOrderResult();
        eipOrderResult.setUserId(reciveOrder.getUserId());
        eipOrderResult.setConsoleOrderFlowId(reciveOrder.getConsoleOrderFlowId());
        eipOrderResult.setOrderId(reciveOrder.getOrderId());

        List<OrderResultProduct> orderResultProducts = new ArrayList<>();
        OrderResultProduct orderResultProduct = new OrderResultProduct();
        if (HsConstants.FAIL.equalsIgnoreCase(result)) {
            orderResultProduct.setProductSetStatus(result);
        } else {
            orderResultProduct.setProductSetStatus(HsConstants.SUCCESS);
        }
        orderResultProduct.setProductList(reciveOrder.getProductList());
        orderResultProducts.add(orderResultProduct);
        eipOrderResult.setProductSetList(orderResultProducts);
        return eipOrderResult;
    }

    /**
     * get SBW config from BSS
     * @return eip param
     */
    private SbwUpdateParam getSbwConfigByOrder(ReciveOrder reciveOrder) {
        SbwUpdateParam updateParam = new SbwUpdateParam();
        updateParam.setBillType(reciveOrder.getBillType());
        updateParam.setDuration(reciveOrder.getDuration());
        List<OrderProduct> productList = reciveOrder.getProductList();
        for (OrderProduct orderProduct : productList) {
            if (!orderProduct.getProductLineCode().equalsIgnoreCase(HsConstants.SBW)) {
                continue;
            }
            updateParam.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItemList = orderProduct.getItemList();

            for (OrderProductItem sbwItem : orderProductItemList) {
                if (sbwItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                    updateParam.setBandwidth(Integer.parseInt(sbwItem.getValue()));
                }else if (sbwItem.getCode().equals(HsConstants.SBW_NAME)) {
                    updateParam.setSbwName(sbwItem.getValue());
                }
            }
        }

        log.info("Get sbw param from bss MQ:{}", updateParam.toString());
        return updateParam;
    }

    /**
     *  extract SBW message from entity to return BSS MQ
     * @param reciveOrder
     * @param result
     * @return
     */
    private EipOrderResult packageSbwReturnResult(ReciveOrder reciveOrder,String sbwId, String result) {
        List<OrderProduct> productList = reciveOrder.getProductList();

        for (OrderProduct orderProduct : productList) {
            orderProduct.setInstanceStatus(result);
            orderProduct.setInstanceId(sbwId);
            orderProduct.setStatusTime(reciveOrder.getStatusTime());
        }

        EipOrderResult eipOrderResult = new EipOrderResult();
        eipOrderResult.setUserId(reciveOrder.getUserId());
        eipOrderResult.setConsoleOrderFlowId(reciveOrder.getConsoleOrderFlowId());
        eipOrderResult.setOrderId(reciveOrder.getOrderId());

        List<OrderResultProduct> orderResultProducts = new ArrayList<>();
        OrderResultProduct resultProduct = new OrderResultProduct();
        if (HsConstants.STATUS_ERROR.equalsIgnoreCase(result)) {
            resultProduct.setProductSetStatus(HsConstants.FAIL);
        } else {
            resultProduct.setProductSetStatus(HsConstants.SUCCESS);
        }
        resultProduct.setProductList(reciveOrder.getProductList());

        orderResultProducts.add(resultProduct);
        eipOrderResult.setProductSetList(orderResultProducts);
        return eipOrderResult;
    }
}
