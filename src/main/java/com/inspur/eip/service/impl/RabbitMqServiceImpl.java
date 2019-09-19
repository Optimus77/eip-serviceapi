package com.inspur.eip.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.*;
import com.inspur.eip.entity.bss.*;
import com.inspur.eip.entity.eip.EipAllocateParam;
import com.inspur.eip.entity.sbw.SbwUpdateParam;
import com.inspur.eip.entity.eip.EipReturnBase;
import com.inspur.eip.entity.sbw.SbwReturnBase;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.SbwDaoService;
import com.inspur.eip.service.WebControllerService;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.constant.ConstantClassField;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.inspur.eip.util.common.CommonUtil.preCheckParam;
import static com.inspur.eip.util.common.CommonUtil.preSbwCheckParam;

/**
 * @Description convert to mq
 * @Author Zerah
 * @Date 2019/5/28 10:59
 **/
@Service
@Slf4j
public class RabbitMqServiceImpl {
    @Autowired
    private RabbitMessagingTemplate rabbitTemplate;

    @Autowired
    private WebControllerService webService;

    @Autowired
    private EipServiceImpl eipService;

    @Autowired
    private EipDaoService eipDaoService;

    @Autowired
    private SbwDaoService sbwDaoService;

    @Autowired
    private SbwServiceImpl sbwService;

    // 发送消息的exchange:订单消息和停服、软删消息使用同一个exchange ，如需更换，需添加停服软删的exchange配置
    @Value("${bss.queues.order.binding.exchange}")
    private String exchange;

    // 发送订单消息的routingKey
    @Value("${bss.queues.order.binding.returnRoutingKey}")
    private String orderKey;

    // 发送停服、软删消息的routingKey
    @Value("${bss.queues.change.binding.returnRoutingKey}")
    private String changeKey;

    /**
     * ==================================================================================================================================
     * \\                                                          EIP                                                                  \\
     * ==================================================================================================================================
     */

    /**
     * create an  eip entity info
     *
     * @param eipOrder order
     * @return return message
     */
    public String createEipInfo(ReciveOrder eipOrder) {
        ResponseEntity<EipReturnBase> response;
        EipReturnBase eipReturn;
        String eipId = null;
        String eipAddress = null;
        String createResult = null;
        log.info("Recive create mq:{}", JSONObject.toJSONString(eipOrder));
        try {
            if (!(eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) ){
                log.warn("Order must by payed successfully.");
                return null;
            }
            List<OrderProduct> orderProducts = eipOrder.getProductList();
            String groupId = null;
            if(eipOrder.getConsoleCustomization().containsKey("groupId")){
                groupId = CommonUtil.getUUID();
            }

            for (OrderProduct orderProduct : orderProducts) {
                if (!orderProduct.getProductLineCode().equals(HsConstants.EIP)) {
                    continue;
                }
                eipId = null;
                eipAddress = null;
                EipAllocateParam eipConfig = getEipConfigByOrder(orderProduct, eipOrder.getBillType(), groupId);
                ReturnMsg checkRet = preCheckParam(eipConfig);
                if ( !(checkRet.getCode().equals(ReturnStatus.SC_OK))) {
                    log.warn(checkRet.getMessage());
                    return null;
                }
                response = eipService.atomCreateEip(eipConfig, eipOrder.getToken(), null);
                if (response.getStatusCodeValue() != HttpStatus.SC_OK) {
                    createResult = HsConstants.FAIL;
                    log.warn("create eip failed, return code:{}", response.getStatusCodeValue());
                } else {
                    eipReturn = response.getBody();
                    if (null != eipReturn) {
                        eipId = eipReturn.getId();
                        eipAddress = eipReturn.getEipAddress();
                    }
                    createResult = HsConstants.SUCCESS;
                }
                webService.retWebsocket(eipConfig.getIpv6(),eipId,eipOrder,
                        "createNatWithEip", "create",response.getStatusCodeValue());
                updateEipOrderResult(orderProduct, eipId,eipAddress, eipOrder.getStatusTime(), groupId, createResult);
            }
        } catch (Exception e) {
            log.error(ConstantClassField.EXCEPTION_EIP_CREATE, e);
            if (null != eipId) {
                eipDaoService.deleteEip(eipId, eipOrder.getToken());
                eipId = null;
            }
        } finally {
            if (null == eipId) {
                sendOrderMessageToBss(getEipOrderResult(eipOrder, HsConstants.FAIL));
            } else {
                sendOrderMessageToBss(getEipOrderResult(eipOrder, HsConstants.SUCCESS));
            }
        }
        return eipId;
    }

    /**
     * delete result form bss
     *
     * @param eipOrder order
     * @return string
     */
    public ActionResponse deleteEipConfig(ReciveOrder eipOrder) {
        String eipId = "";
        ActionResponse response = null;
        String deleteResult = HsConstants.FAIL;

        try {
            log.info("Recive delete eip order:{}", JSONObject.toJSONString(eipOrder));
            if (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                List<OrderProduct> orderProducts = eipOrder.getProductList();
                for (OrderProduct orderProduct : orderProducts) {
                    if(!orderProduct.getProductLineCode().equalsIgnoreCase(HsConstants.EIP)){
                        continue;
                    }
                    eipId = orderProduct.getInstanceId();
                    if(CommonUtil.isSuperAccount(eipOrder.getToken())){
                        response = eipDaoService.adminDeleteEip(eipId);
                    }else {
                        //软删除实例，用户主动发起，必须带token
                        response = eipDaoService.deleteEip(eipId, eipOrder.getToken());
                    }
                    if (response.isSuccess()) {
                        if (null != eipOrder.getConsoleCustomization() && eipOrder.getConsoleCustomization().containsKey("operateType") &&
                                eipOrder.getConsoleCustomization().getString("operateType").equalsIgnoreCase("deleteNatWithEip")) {
                            webService.returnsIpv6Websocket("Success", "deleteNatWithEip", eipOrder.getToken());
                        } else {
                            webService.returnsWebsocket(eipId, eipOrder, "delete");
                        }
                        deleteResult = HsConstants.UNSUBSCRIBE;
                    } else {
                        log.warn(ConstantClassField.DELETE_EIP_CONFIG_FAILED, response.getFault() + ReturnStatus.SC_INTERNAL_SERVER_ERROR);
                        deleteResult = HsConstants.FAIL;
                    }
                    updateEipOrderResult(orderProduct, eipId, null,eipOrder.getStatusTime(), null,deleteResult);
                }
                sendOrderMessageToBss(getEipOrderResult(eipOrder, deleteResult));
                return response;
            } else {
                log.error(ConstantClassField.ORDER_STATUS_NOT_CORRECT + eipOrder.getOrderStatus());
            }
        } catch (Exception e) {
            sendOrderMessageToBss(getEipOrderResult(eipOrder, HsConstants.FAIL));
            log.error(ConstantClassField.EXCEPTION_EIP_DELETE, e);
        }
        log.warn(ConstantClassField.UPDATE_EIP_CONFIG_FAILED, response);
        sendOrderMessageToBss(getEipOrderResult(eipOrder, HsConstants.FAIL));
        return response;
    }

    /**
     * update eip config
     *
     * @param eipOrder order
     * @return string
     */
    public ActionResponse updateEipInfoConfig(ReciveOrder eipOrder) {
        String eipId = null;
        ActionResponse response = null;
        String result = HsConstants.FAIL;
        int failedCount=0;
        try {
            log.info("Recive update Eip order:{}", JSONObject.toJSONString(eipOrder));

            if ((eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS))) {
                List<OrderProduct> orderProducts = eipOrder.getProductList();
                for (OrderProduct orderProduct : orderProducts) {
                    if (!orderProduct.getProductLineCode().equals(HsConstants.EIP)) {
                        continue;
                    }
                    eipId = orderProduct.getInstanceId();
                    EipUpdateParam eipUpdate = getUpdateParmByOrder(orderProduct, eipOrder.getBillType(), eipOrder.getDuration());
                    //更配操作
                    if (eipOrder.getOrderType().equalsIgnoreCase(HsConstants.CHANGECONFIGURE_ORDERTYPE)) {
                        if (eipUpdate.getSbwId() != null) {
                            if (eipUpdate.getChargemode().equalsIgnoreCase(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH)) {
                                response = sbwDaoService.addEipIntoSbw(eipId, eipUpdate, eipOrder.getToken());
                                log.info("add eip to sbw:{}", response);
                            } else if (eipUpdate.getChargemode().equalsIgnoreCase(HsConstants.CHARGE_MODE_BANDWIDTH)) {
                                response = sbwDaoService.removeEipFromSbw(eipId, eipUpdate, eipOrder.getToken());
                                log.info("remove eip from sbw:{}", response);
                            }
                        } else if (eipUpdate.getBillType().equals(HsConstants.MONTHLY) ||
                                eipUpdate.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                            response = eipDaoService.updateEipEntity(eipId, eipUpdate, eipOrder.getToken());
                        } else {
                            log.error(ConstantClassField.BILL_TYPE_NOT_SUPPORT, eipOrder.getOrderType());
                        }
                        //用户主动发起的包年包月实例续费操作
                    } else if (eipOrder.getOrderType().equalsIgnoreCase(HsConstants.RENEW_ORDERTYPE) && eipOrder.getBillType().equals(HsConstants.MONTHLY)) {
                        response = eipDaoService.reNewEipEntity(eipId, eipOrder.getDuration(), eipOrder.getToken());
                        //不支持的订单类型
                    } else {
                        log.error(ConstantClassField.ORDER_TYPE_NOT_SUPPORT, eipOrder.getOrderType());
                    }
                    if (response == null || !response.isSuccess()) {
                        failedCount += 1;
                        result = HsConstants.FAIL;
                    }else {
                        result = HsConstants.SUCCESS;
                    }
                    updateEipOrderResult(orderProduct,eipId,null,eipOrder.getStatusTime(), null, result);
                }
                if (failedCount == 0) {
                    result = HsConstants.SUCCESS;
                    webService.returnsWebsocket(eipId, eipOrder, "update");
                    sendOrderMessageToBss(getEipOrderResult(eipOrder, result));
                    return response;
                }else {
                    result = HsConstants.FAIL;
                }
            } else {
                log.error(ConstantClassField.ORDER_STATUS_NOT_CORRECT + eipOrder.getOrderStatus());
            }
        } catch (Exception e) {
            log.error(ConstantClassField.EXCEPTION_EIP_UPDATE, e);
            sendOrderMessageToBss(getEipOrderResult(eipOrder, HsConstants.FAIL));
        }
        log.warn(ConstantClassField.UPDATE_EIP_CONFIG_FAILED, response);
        webService.returnsWebsocket(eipId, eipOrder, "update");
        sendOrderMessageToBss(getEipOrderResult(eipOrder, result));
        return response;
    }

    /**
     * stop server or soft delete order from bss
     * @param eipOrder order
     * @return return
     */
    public ActionResponse softDowOrDeleteEip(OrderSoftDown eipOrder) {
        ActionResponse response = null;
        String result = HsConstants.FAIL;
        String insanceStatus = HsConstants.FAIL;
        try {
            log.info("Recive soft down or delete Eip order:{}", JSONObject.toJSONString(eipOrder));
            List<SoftDownInstance> instanceList = eipOrder.getInstanceList();
            for (SoftDownInstance softDownInstance : instanceList) {
                String operateType = softDownInstance.getOperateType();
                //订单测发起的软删操作，不带token，需要通过admin权限操作
                if (HsConstants.DELETE.equalsIgnoreCase(operateType)) {
                    response = eipDaoService.adminDeleteEip(softDownInstance.getInstanceId());
                    if (response.isSuccess()) {
                        insanceStatus = HsConstants.DELETED;
                        result = HsConstants.SUCCESS;
                    }
                    //订单测主动发起的停服操作，不带toekn,即包年包月实例到期 ，订单测发起停服订单
                } else if (HsConstants.STOPSERVER.equalsIgnoreCase(operateType)) {
                    response = eipDaoService.softDownEip(softDownInstance.getInstanceId());
                    if (response.isSuccess()) {
                        insanceStatus = HsConstants.STOPSERVER;
                        result = HsConstants.SUCCESS;
                    } else if (response.getCode() == HttpStatus.SC_NOT_FOUND) {
                        insanceStatus = HsConstants.NOTFOUND;
                        result = HsConstants.SUCCESS;
                    }
                    //停服重开，即停服之后，用户又进行了充值操作，订单测主动发起重开请求，业务侧进行续费操作，不带token
                } else if (HsConstants.RESUMESERVER.equalsIgnoreCase(operateType)) {
                    response = eipDaoService.reNewEipEntity(softDownInstance.getInstanceId(), "1");
                    if (response != null && response.isSuccess()) {
                        insanceStatus = HsConstants.SUCCESS;
                        result = HsConstants.SUCCESS;
                    }
                } else {
                    continue;
                }
                softDownInstance.setResult(result);
                softDownInstance.setInstanceStatus(insanceStatus);
                softDownInstance.setStatusTime(CommonUtil.getBeiJTime());
            }
            log.info(ConstantClassField.SOFTDOWN_OR_DELETE_EIP_CONFIG_RESULT, response);
            sendChangeMessageToBss(eipOrder);
        } catch (Exception e) {
            log.error(ConstantClassField.EXCEPTION_EIP_SOFTDOWN_OR_DELETE, e);
        }
        return response;
    }
    /**
     * ==================================================================================================================================
     * \\                                                          SBW                                                                  \\
     * \\                     Bss暂支持的InstanceStatus状态                 productSetStatus状态(不一一对应)                             \\
     * \\                         ACTIVE                                         success                                                \\
     * \\                         ERROR                                           fail                                                  \\
     * \\                         PENDING_CREATE                                                                                        \\
     * \\                         STOP                                                                                                  \\
     * \\                         DELETE                                                                                                \\
     * ==================================================================================================================================
     */


    /**
     * get create shareband result
     * @return return message
     */
    public ResponseEntity createSbwInfo(ReciveOrder reciveOrder) {
        ResponseEntity<SbwReturnBase> response = null;
        SbwReturnBase sbwReturn;
        String sbwId = null;
        try {
            log.info("Recive create sbw order:{}", JSONObject.toJSONString(reciveOrder));
            //订单状态，必须支付成功
            if (reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParam sbwConfig = getSbwConfigByOrder(reciveOrder);
                ReturnMsg checkRet = preSbwCheckParam(sbwConfig);
                if (checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                    response = sbwService.atomCreateSbw(sbwConfig, reciveOrder.getToken());
                    if (response.getStatusCodeValue() != HttpStatus.SC_OK) {
                        log.warn("create sbw failed, return code:{}", response.getStatusCodeValue());
                    } else {
                        sbwReturn = response.getBody();
                        if (null != sbwReturn) {
                            sbwId = sbwReturn.getId();
                        }
                        webService.returnSbwWebsocket(sbwId, reciveOrder, "create");
                    }
                } else {
                    log.warn(checkRet.getMessage());
                }
            } else {
                log.warn(ConstantClassField.ORDER_STATUS_NOT_CORRECT);
            }
        } catch (Exception e) {
            if (sbwId != null) {
                sbwService.deleteSbwInfo(sbwId, reciveOrder.getToken());
                sbwId = null;
            }
            log.error(ConstantClassField.EXCEPTION_SBW_CREATE, e);
        } finally {
            if (null == sbwId) {
                sendOrderMessageToBss(getSbwReturnResult(reciveOrder, "", HsConstants.STATUS_ERROR));
            } else {
                sendOrderMessageToBss(getSbwReturnResult(reciveOrder, sbwId, HsConstants.STATUS_ACTIVE));
            }
        }
        return response;
    }

    /**
     * delete result from bss
     *
     * @param reciveOrder order
     * @return string
     */
    public ActionResponse deleteSbwConfig(ReciveOrder reciveOrder) {
        String sbwId = "";
        ActionResponse response = null;
        String result = HsConstants.STATUS_ERROR;
        try {
            log.info("Recive delete sbw order:{}", JSONObject.toJSONString(reciveOrder));
            if (reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                List<OrderProduct> productList = reciveOrder.getProductList();
                for (OrderProduct product : productList) {
                    sbwId = product.getInstanceId();
                }
                //业务侧执行软删操作，即按需退订，用户主动发起，带token
                response = sbwService.deleteSbwInfo(sbwId, reciveOrder.getToken());
                if (response.isSuccess() || HsConstants.STATUS_CODE_404==response.getCode()) {
                    result = HsConstants.STATUS_DELETE;
                    webService.returnSbwWebsocket(sbwId, reciveOrder, "delete");
                    sendOrderMessageToBss(getSbwReturnResult(reciveOrder, sbwId, result));
                    return response;
                } else {
                    log.warn("delete sbw failed, return code:{}" + response.getFault());
                }
            } else {
                log.warn(ConstantClassField.ORDER_STATUS_NOT_CORRECT);
            }
        } catch (Exception e) {
            sendOrderMessageToBss(getSbwReturnResult(reciveOrder, sbwId, HsConstants.STATUS_ERROR));
            log.error(ConstantClassField.EXCEPTION_SBW_DELETE, e);
        }
        webService.returnSbwWebsocket(sbwId, reciveOrder, "delete");
        sendOrderMessageToBss(getSbwReturnResult(reciveOrder, sbwId, result));
        log.warn(ConstantClassField.DELETE_SBW_CONFIG_FAILED);
        return response;
    }

    /**
     * update the sbw config,incloud bandWidth and eip
     *
     * @param recive info recived.
     * @return ret
     */
    public ActionResponse updateSbwInfoConfig(ReciveOrder recive) {
        String retStr = HsConstants.STATUS_ERROR;
        ActionResponse response = ActionResponse.actionFailed(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        String sbwId = recive.getProductList().get(0).getInstanceId();
        try {
            log.info("Update sbw config:{}", JSONObject.toJSONString(recive));
            if (recive.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParam sbwUpdate = getSbwConfigByOrder(recive);
                //sbw 实例更配操作，目前只支持更配带宽
                if (recive.getOrderType().equalsIgnoreCase(HsConstants.CHANGECONFIGURE_ORDERTYPE)) {
                    response = sbwService.updateSbwConfig(sbwId, sbwUpdate, recive.getToken());
                    //续费操作，用户主动发起，即包年包月实例，用户可以主动发起续费请求，带token
                } else if (recive.getOrderType().equalsIgnoreCase(HsConstants.RENEW_ORDERTYPE) && recive.getBillType().equals(HsConstants.MONTHLY)) {
                    response = sbwService.restartSbwService(sbwId, sbwUpdate, recive.getToken());
                    //不支持的订单类型
                } else {
                    log.warn(ConstantClassField.ORDER_STATUS_NOT_CORRECT, recive.getOrderType());
                }
                if (response.isSuccess()) {
                    retStr = HsConstants.STATUS_ACTIVE;
                    webService.returnSbwWebsocket(sbwId, recive, "update");
                    sendOrderMessageToBss(getSbwReturnResult(recive, sbwId, retStr));
                    log.info(ConstantClassField.UPDATE_SBW_CONFIG_SUCCESS, response);
                    return response;
                } else {
                    log.warn(ConstantClassField.OPERATION_RESULT_NOT_OK, response);
                }
            }
        } catch (Exception e) {
            sendOrderMessageToBss(getSbwReturnResult(recive, sbwId, HsConstants.STATUS_ERROR));
            log.error(ConstantClassField.EXCEPTION_SBW_UPDATE, e);
        }
        webService.returnSbwWebsocket(sbwId, recive, "update");
        sendOrderMessageToBss(getSbwReturnResult(recive, sbwId, retStr));
        log.warn(ConstantClassField.SOFTDOWN_OR_DELETE_SBW_CONFIG_RESULT, response);
        return response;
    }

    /**
     * 停服或者删除共享带宽
     *
     * @param softDown softdown
     * @return ret
     */
    public ActionResponse softDowOrDeleteSbw(OrderSoftDown softDown) {
        String setStatus = HsConstants.FAIL;
        String instanceStatus = HsConstants.STATUS_ERROR;
        ActionResponse response = null;
        try {
            log.info("Recive soft down or delete SBW order:{}", JSONObject.toJSONString(softDown));
            List<SoftDownInstance> instanceList = softDown.getInstanceList();
            for (SoftDownInstance instance : instanceList) {
                String operateType = instance.getOperateType();
                //订单测主动发起的软删请求，不带toekn
                if (HsConstants.DELETE.equalsIgnoreCase(operateType)) {
                    response = sbwService.bssSoftDeleteSbw(instance.getInstanceId());
                    if (response.isSuccess() || response.getCode() == HttpStatus.SC_NOT_FOUND ) {
                        setStatus = HsConstants.SUCCESS;
                        instanceStatus = HsConstants.STATUS_DELETE;
                    }
                    //订单测主动发起的停服操作，不带toekn,即包年包月实例到期 ，订单测发起停服请求
                } else if (HsConstants.STOPSERVER.equalsIgnoreCase(operateType)) {
                    SbwUpdateParam updateParam = new SbwUpdateParam();
                    updateParam.setDuration("0");
                    response = sbwService.stopSbwService(instance.getInstanceId(), updateParam);
                    if (response.isSuccess() || response.getCode() == HttpStatus.SC_NOT_FOUND) {
                        setStatus = HsConstants.SUCCESS;
                        instanceStatus = HsConstants.STATUS_STOP;
                    }
                    //停服重开
                } else if (HsConstants.RESUMESERVER.equalsIgnoreCase(operateType)) {
                    response = sbwDaoService.resumeSbwInfo(instance.getInstanceId());
                    if (response != null && response.isSuccess()) {
                        setStatus = HsConstants.SUCCESS;
                        instanceStatus = HsConstants.ACTIVE;
                    }
                } else {
                    continue;
                }
                instance.setResult(setStatus);
                instance.setInstanceStatus(instanceStatus);
                instance.setStatusTime((CommonUtil.getBeiJTime()));
            }
            sendChangeMessageToBss(softDown);
            log.info(ConstantClassField.SOFTDOWN_OR_DELETE_SBW_CONFIG_RESULT, response);
        } catch (Exception e) {
            log.error(ConstantClassField.EXCEPTION_SBW_SOFTDOWN_OR_DELETE, e);
        }
        return response;
    }

    /**
     * extract create eip config from BSS MQ
     * @param orderProduct  prd
     * @param billType  bill
     * @param groupId  group id
     * @return  ret
     */
    private EipAllocateParam getEipConfigByOrder(OrderProduct orderProduct, String billType, String groupId) {

        EipAllocateParam eipAllocateParam = new EipAllocateParam();
        eipAllocateParam.setBillType(billType);
        eipAllocateParam.setChargeMode(HsConstants.CHARGE_MODE_BANDWIDTH);

        eipAllocateParam.setRegion(orderProduct.getRegion());
        List<OrderProductItem> orderProductItems = orderProduct.getItemList();

        for (OrderProductItem orderProductItem : orderProductItems) {
            if (orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                eipAllocateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
            } else if (orderProductItem.getCode().equals(HsConstants.PROVIDER)) {
                eipAllocateParam.setIpType(orderProductItem.getValue());
            } else if (orderProductItem.getCode().equals(HsConstants.IS_SBW) &&
                    orderProductItem.getValue().equals(HsConstants.YES)) {
                eipAllocateParam.setChargeMode(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH);
            } else if (orderProductItem.getCode().equals(HsConstants.WITH_IPV6) &&
                    orderProductItem.getValue().equals(HsConstants.YES)) {
                eipAllocateParam.setIpv6("yes");
            } else if (orderProductItem.getCode().equals(HsConstants.SBW_ID)) {
                eipAllocateParam.setSbwId(orderProductItem.getValue());
            }
        }
        eipAllocateParam.setGroupId(groupId);

        log.info("Get eip param from order:{}", JSONObject.toJSONString(eipAllocateParam));
        return eipAllocateParam;
    }

    private EipUpdateParam getUpdateParmByOrder(OrderProduct orderProduct, String billType, String duration) {

        EipUpdateParam eipUpdateParam = new EipUpdateParam();
        eipUpdateParam.setBillType(billType);
        eipUpdateParam.setDuration(duration);
        // 默认为带宽计费
        eipUpdateParam.setChargemode(HsConstants.CHARGE_MODE_BANDWIDTH);

        List<OrderProductItem> orderProductItems = orderProduct.getItemList();

        for (OrderProductItem orderProductItem : orderProductItems) {
            if (orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                eipUpdateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
            } else if (orderProductItem.getCode().equals(HsConstants.IS_SBW) &&
                    orderProductItem.getValue().equalsIgnoreCase(HsConstants.YES)) {
                eipUpdateParam.setChargemode(HsConstants.CHARGE_MODE_SHAREDBANDWIDTH);
            } else if (orderProductItem.getCode().equals(HsConstants.SBW_ID)) {
                eipUpdateParam.setSbwId(orderProductItem.getValue());
            }
        }

        log.debug("Get eip param from bss MQ:{}", eipUpdateParam.toString());
        return eipUpdateParam;
    }

    /**
     * extract EIP message from entity to return BSS MQ
     *
     * @param reciveOrder order
     * @return Console2BssResult
     */
    private Console2BssResult getEipOrderResult(ReciveOrder reciveOrder,  String result) {
        //must not be delete ,set the reference
        Console2BssResult console2BssResult = new Console2BssResult();
        console2BssResult.setUserId(reciveOrder.getUserId());
        console2BssResult.setConsoleOrderFlowId(reciveOrder.getConsoleOrderFlowId());
        console2BssResult.setOrderId(reciveOrder.getOrderId());

        List<OrderResultProduct> orderResultProducts = new ArrayList<>();
        OrderResultProduct orderResultProduct = new OrderResultProduct();
        if (HsConstants.FAIL.equalsIgnoreCase(result)) {
            orderResultProduct.setProductSetStatus(result);
        } else {
            orderResultProduct.setProductSetStatus(HsConstants.SUCCESS);
        }
        orderResultProduct.setProductList( reciveOrder.getProductList());
        orderResultProducts.add(orderResultProduct);
        console2BssResult.setProductSetList(orderResultProducts);
        return console2BssResult;
    }

    private void updateEipOrderResult(OrderProduct orderProduct, String eipId, String eipAddress, String createIime,String groupId, String result) {
        //must not be delete ,set the reference
        int groupFlag = 0;

        orderProduct.setInstanceId(eipId);
        orderProduct.setStatusTime(createIime);
        orderProduct.setInstanceStatus(result);
        if(groupId != null) {
            List<OrderProductItem> items = orderProduct.getItemList();
            for(OrderProductItem orderProductItem: items){
                if(orderProductItem.getCode().equalsIgnoreCase(HsConstants.GROUP_ID)){
                    orderProductItem.setValue(groupId);
                    groupFlag = 1;
                }else if(orderProductItem.getCode().equalsIgnoreCase(HsConstants.IP)){
                    if(null != eipAddress) {
                        orderProductItem.setValue(eipAddress);
                    }
                }
            }
            if(0 == groupFlag) {
                OrderProductItem orderProductItem = new OrderProductItem();
                orderProductItem.setCode(HsConstants.GROUP_ID);
                orderProductItem.setValue(groupId);
                orderProduct.getItemList().add(orderProductItem);
            }
        }
    }

    /**
     * get SBW config from BSS
     *
     * @return eip param
     */
    private SbwUpdateParam getSbwConfigByOrder(ReciveOrder reciveOrder) {
        SbwUpdateParam updateParam = new SbwUpdateParam();
        updateParam.setBillType(reciveOrder.getBillType());
        updateParam.setDuration(reciveOrder.getDuration());
        JSONObject customization = reciveOrder.getConsoleCustomization();
        if (customization!=null){
            String description = customization.getString("description");
            if (StringUtils.isNotBlank(description)){
                updateParam.setDescription(description);
            }
        }
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
                } else if (sbwItem.getCode().equals(HsConstants.SBW_NAME)) {
                    updateParam.setSbwName(sbwItem.getValue());
                }else if (sbwItem.getCode().equals(HsConstants.PROVIDER)){
                    updateParam.setIpType(sbwItem.getValue());
                }
            }
        }

        log.debug("Get sbw param from bss MQ:{}", updateParam.toString());
        return updateParam;
    }

    /**
     * extract SBW message from entity to return BSS MQ
     *
     * @param reciveOrder
     * @param result
     * @return
     */
    private Console2BssResult getSbwReturnResult(ReciveOrder reciveOrder, String sbwId, String result) {
        List<OrderProduct> productList = reciveOrder.getProductList();

        for (OrderProduct orderProduct : productList) {
            orderProduct.setInstanceStatus(result);
            orderProduct.setInstanceId(sbwId);
            orderProduct.setStatusTime(reciveOrder.getStatusTime());
        }

        Console2BssResult console2BssResult = new Console2BssResult();
        console2BssResult.setUserId(reciveOrder.getUserId());
        console2BssResult.setConsoleOrderFlowId(reciveOrder.getConsoleOrderFlowId());
        console2BssResult.setOrderId(reciveOrder.getOrderId());

        List<OrderResultProduct> orderResultProducts = new ArrayList<>();
        OrderResultProduct resultProduct = new OrderResultProduct();
        if (HsConstants.STATUS_ERROR.equalsIgnoreCase(result)) {
            resultProduct.setProductSetStatus(HsConstants.FAIL);
        } else {
            resultProduct.setProductSetStatus(HsConstants.SUCCESS);
        }
        resultProduct.setProductList(reciveOrder.getProductList());

        orderResultProducts.add(resultProduct);
        console2BssResult.setProductSetList(orderResultProducts);
        return console2BssResult;
    }


    private void sendOrderMessageToBss(Console2BssResult obj) {
        // 这里会用rabbitMessagingTemplate中配置的MessageConverter自动将obj转换为字节码
        log.info("+++++++Send Order message to Console：+++++++:{}", JSONObject.toJSONString(obj));
        rabbitTemplate.convertAndSend(exchange, orderKey, obj);
    }

    private void sendChangeMessageToBss(OrderSoftDown obj) {
        log.info("-------Send Change message to Console：-------:{}", JSONObject.toJSONString(obj));
        rabbitTemplate.convertAndSend(exchange, changeKey, obj);
    }

}
