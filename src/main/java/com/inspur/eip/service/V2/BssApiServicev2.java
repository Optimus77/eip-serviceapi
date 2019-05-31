package com.inspur.eip.service.V2;


import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.*;
import com.inspur.eip.entity.sbw.*;
import com.inspur.eip.entity.v2.eip.EipReturnBase;
import com.inspur.eip.entity.v2.sbw.SbwReturnBase;
import com.inspur.eip.service.impl.EipServiceImpl;
import com.inspur.eip.service.impl.SbwServiceImpl;
import com.inspur.eip.util.HsConstants;
import com.inspur.eip.util.ReturnMsgUtil;
import com.inspur.eip.util.ReturnResult;
import com.inspur.eip.util.ReturnStatus;
import com.inspur.eip.util.v2.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


import static com.inspur.eip.util.CommonUtil.preCheckParam;
import static com.inspur.eip.util.CommonUtil.preSbwCheckParam;

@Service
@Slf4j
public class BssApiServicev2 {


    @Autowired
    private WebControllerServicev2 webControllerService;


    @Autowired
    private EipServiceImpl eipService;

    @Autowired
    private SbwServiceImpl sbwService;

    /**
     * get create order result
     * @param eipOrder order
     * @return return message
     */
    public ResponseEntity onReciveCreateOrderResult(ReciveOrder eipOrder) {

        String code;
        String msg;
        String eipId = "";
        ResponseEntity createRet = null;
        ReturnResult returnResult = null;
        try {
            log.debug("Recive create order:{}", JSONObject.toJSONString(eipOrder));
            if (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                EipAllocateParam eipConfig = getEipConfigByOrder(eipOrder);
                ReturnMsg checkRet = preCheckParam(eipConfig);
                if (checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                    //post request to atom
                    EipAllocateParamWrapper eipAllocateParamWrapper = new EipAllocateParamWrapper();
                    eipAllocateParamWrapper.setEip(eipConfig);
                    createRet = eipService.atomCreateEip(eipAllocateParamWrapper.getEip());
                    String retStr = HsConstants.SUCCESS;

                    if (createRet.getStatusCodeValue() != HttpStatus.SC_OK) {
                        retStr = HsConstants.FAIL;
                        log.info("create eip failed, return code:{}", createRet.getStatusCodeValue());
                    } else {
                        com.inspur.eip.entity.v2.ReturnMsg body =(com.inspur.eip.entity.v2.ReturnMsg)createRet.getBody();
                        EipReturnBase eip = (EipReturnBase)body.getEip();
                        eipId =eip.getEipId();
                        webControllerService.returnsWebsocket(eipId, eipOrder, "create");
                        if (eipConfig.getIpv6().equalsIgnoreCase("yes")) {
                            webControllerService.returnsIpv6Websocket("Success", "Success", "createNatWithEip");
                        }
                    }
                    returnResult = webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, retStr));

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
        } finally {
            if ((null == returnResult) || (!returnResult.isSuccess())) {
                if ((null != createRet) && (createRet.getStatusCodeValue() == HttpStatus.SC_OK)) {
                    log.error("Delete the allocate eip just now for mq message error, id:{}", eipId);
                    eipService.atomDeleteEip(eipId);
                }
            }
        }
        webControllerService.resultReturnMq(getEipOrderResult(eipOrder, "", HsConstants.FAIL));
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * delete result form bss
     * @param eipOrder order
     * @return string
     */
    public ResponseEntity onReciveDeleteOrderResult(ReciveOrder eipOrder,String eipId) {
        String msg;
        String code;
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(eipOrder));
            if (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {

                ResponseEntity  delResult = eipService.atomDeleteEip(eipId);

                if (delResult.getStatusCodeValue() == org.springframework.http.HttpStatus.OK.value()) {
                    if (eipOrder.getConsoleCustomization().containsKey("operateType")
                            && eipOrder.getConsoleCustomization().getString("operateType").equalsIgnoreCase("deleteNatWithEip")) {
                        webControllerService.returnsIpv6Websocket("Success", "Success", "deleteNatWithEip");
                    } else {
                        webControllerService.returnsWebsocket(eipId, eipOrder, "delete");
                    }
                    webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.UNSUBSCRIBE));
                    return delResult;
                } else {
                    msg = "HTTP false";
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
        webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.FAIL));
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * update order from bss
     * @param eipId    id
     * @param eipOrder order
     * @return string
     */
    public ResponseEntity onReciveUpdateOrder(String eipId, ReciveOrder eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;

        try {
            log.debug("Recive update order:{}", JSONObject.toJSONString(eipOrder));

            if ((null != eipOrder) && (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS))) {
                EipUpdateParam eipUpdate = getUpdatParmByOrder(eipOrder);
                ResponseEntity updateRet=null;
                if (eipOrder.getOrderType().equalsIgnoreCase("changeConfigure")) {
                    updateRet = eipService.updateEipBandWidth(eipId, eipUpdate);
                } else if (eipOrder.getOrderType().equalsIgnoreCase("renew") && eipOrder.getBillType().equals(HsConstants.MONTHLY)) {
                    updateRet = eipService.renewEip(eipId, eipUpdate);
                } else {
                    log.error("Not support order type:{}", eipOrder.getOrderType());
                }
                String retStr = HsConstants.SUCCESS;
                if(updateRet!=null){
                    if (updateRet.getStatusCodeValue()!= HttpStatus.SC_OK) {
                        retStr = HsConstants.FAIL;
                    }
                }

                log.debug("renew order result :{}", updateRet);
                webControllerService.returnsWebsocket(eipId, eipOrder, "update");
                webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, retStr));
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        if (null != eipOrder) {
            webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.FAIL));
        }
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * soft down order from bss
     * @param eipOrder order
     * @return return
     */
    public ResponseEntity onReciveSoftDownOrder(OrderSoftDown eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        ResponseEntity updateRet = null;
        String retStr;;
        String iStatusStr;
        try {
            log.debug("Recive soft down order:{}", JSONObject.toJSONString(eipOrder));
            List<SoftDownInstance> instanceList = eipOrder.getInstanceList();
            for (SoftDownInstance softDownInstance : instanceList) {
                String operateType = softDownInstance.getOperateType();
                if ("delete".equalsIgnoreCase(operateType)) {
                    updateRet = eipService.atomDeleteEip(softDownInstance.getInstanceId());
                    iStatusStr = HsConstants.DELETED;
                    retStr = HsConstants.SUCCESS;
                    if (updateRet.getStatusCodeValue()!=  HttpStatus.SC_OK){
                        retStr = HsConstants.FAIL;
                        iStatusStr = HsConstants.FAIL;
                    }
                } else if (HsConstants.STOPSERVER.equalsIgnoreCase(operateType)) {
                    EipUpdateParam updateParam = new EipUpdateParam();
                    updateParam.setDuration("0");
                    updateRet = eipService.renewEip(softDownInstance.getInstanceId(), updateParam);
                    if (updateRet.getStatusCodeValue() == HttpStatus.SC_OK){
                        iStatusStr = HsConstants.STOPSERVER;
                        retStr = HsConstants.SUCCESS;
                    }else if(updateRet.getStatusCodeValue() == HttpStatus.SC_NOT_FOUND){
                        iStatusStr = HsConstants.NOTFOUND;
                        retStr = HsConstants.SUCCESS;
                    } else {
                        retStr = HsConstants.FAIL;
                        iStatusStr = HsConstants.FAIL;
                    }
                }else if ("resumeServer".equalsIgnoreCase(operateType)){
                    EipUpdateParam eipUpdate = new EipUpdateParam();
                    eipUpdate.setDuration("1");
                    updateRet = eipService.renewEip(softDownInstance.getInstanceId(), eipUpdate);
                    if (updateRet.getStatusCodeValue() == HttpStatus.SC_OK){
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
                webControllerService.resultReturnNotify(eipOrder);
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        webControllerService.resultReturnNotify(eipOrder);
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * get eip config from order
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
     * get eip config from order
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
        log.info("Get eip param from order:{}", eipAllocateParam.toString());
        /*chargemode now use the default value */
        return eipAllocateParam;
    }

    /**
     * constructe EipOrderResult form order
     * @param reciveOrder order
     * @param eipId       id
     * @param result      result
     * @return EipOrderResult
     */
    private EipOrderResult getEipOrderResult(ReciveOrder reciveOrder, String eipId, String result) {
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
     * get create shareband result
     * @return return message
     */
    public ResponseEntity createShareBandWidth(ReciveOrder reciveOrder) {

        String code;
        String msg;
        String sbwId = "";
        ResponseEntity createRet = null;
        ReturnResult returnResult = null;
        try {
            if (reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParam sbwConfig = getSbwConfigByOrder(reciveOrder);
                ReturnSbwMsg checkRet = preSbwCheckParam(sbwConfig);
                if (checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                    //post request to atom
                    SbwUpdateParamWrapper sbwWrapper = new SbwUpdateParamWrapper();
                    sbwWrapper.setSbw(sbwConfig);
                    createRet = sbwService.atomCreateSbw(sbwConfig);
                    String retStr = HsConstants.STATUS_ACTIVE;

                    if (createRet.getStatusCodeValue() != HttpStatus.SC_OK) {
                        retStr = HsConstants.STATUS_ERROR;
                        log.info("create sbw failed, return code:{}", createRet.getStatusCodeValue());
                    } else {
                        com.inspur.eip.entity.v2.ReturnMsg body =(com.inspur.eip.entity.v2.ReturnMsg)createRet.getBody();
                        SbwReturnBase sbw = (SbwReturnBase)body.getEip();
                        webControllerService.returnSbwWebsocket(sbw.getSbwId(), reciveOrder, "create");
                    }
                    returnResult = webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId, retStr));

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
            log.error("Exception in createSbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        } finally {
            if ((null == returnResult) || (!returnResult.isSuccess())) {
                if ((null != createRet) && (createRet.getStatusCodeValue() == HttpStatus.SC_OK)) {
                    log.error("Delete the allocate sbw just now for mq message error, id:{}", sbwId);
                    sbwService.atomDeleteSbw(sbwId);
                }
            }
        }
        webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, "", HsConstants.STATUS_ERROR));
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * delete result from bss
     * @param reciveOrder order
     * @return string
     */
    public ResponseEntity deleteShareBandWidth(ReciveOrder reciveOrder) {
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
                ResponseEntity delResult = sbwService.atomDeleteSbw(sbwId);

                if (delResult.getStatusCodeValue() == HttpStatus.SC_OK) {
                    //Return message to the front des
                    webControllerService.returnSbwWebsocket(sbwId, reciveOrder, "delete");
                    webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId, HsConstants.STATUS_DELETE));
                    return delResult;
                } else {
                    msg = delResult.getStatusCode().toString();
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
        webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId, HsConstants.STATUS_ERROR));
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * update the sbw config,incloud bandWidth and eip
     * @param sbwId  id
     * @param recive info recived
     * @return ret
     */
    public ResponseEntity updateSbwConfig(String sbwId, ReciveOrder recive) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        String retStr = HsConstants.STATUS_ACTIVE;
        try {
            log.info("Update sbw config:{}", JSONObject.toJSONString(recive));
            if (recive.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                SbwUpdateParam sbwUpdate = getSbwUpdatParmByOrder(recive);
                wrapper.setSbw(sbwUpdate);
                ResponseEntity updateRet=null;
                if (recive.getOrderType().equalsIgnoreCase("changeConfigure")) {
                    updateRet = sbwService.updateSbwBandWidth(sbwId, sbwUpdate);
                } else if (recive.getOrderType().equalsIgnoreCase("renew") && recive.getBillType().equals(HsConstants.MONTHLY)) {
                    updateRet = sbwService.renewSbw(sbwId, sbwUpdate);
                } else {
                    log.error("Not support order type:{}", recive.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                if (updateRet.getStatusCodeValue() != HttpStatus.SC_OK) {
                    retStr = HsConstants.STATUS_ERROR;
                }else {
                    log.info("update order result :{}", updateRet);
                    webControllerService.returnSbwWebsocket(sbwId, recive, "update");
                }
                 webControllerService.resultSbwReturnMq(getSbwResult(recive, sbwId, retStr));
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in update sbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        webControllerService.resultSbwReturnMq(getSbwResult(recive, sbwId, retStr));
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity stopOrSoftDeleteSbw(OrderSoftDown softDown) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        ResponseEntity updateRet = null;
        String setStatus = HsConstants.SUCCESS;

        String instanceStatusStr = "";
        try {
            log.debug("Recive soft down or delete order:{}", JSONObject.toJSONString(softDown));
            List<SoftDownInstance> instanceList = softDown.getInstanceList();
            for (SoftDownInstance instance : instanceList) {
                String operateType = instance.getOperateType();
                if ("delete".equalsIgnoreCase(operateType)) {
                    updateRet = sbwService.atomDeleteSbw(instance.getInstanceId());
                } else if ("stopServer".equalsIgnoreCase(operateType)) {
                    SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                    SbwUpdateParam updateParam = new SbwUpdateParam();
                    updateParam.setDuration("0");
                    wrapper.setSbw(updateParam);
                    updateRet = sbwService.renewSbw(instance.getInstanceId(), updateParam);
                } else {
                    continue;
                }

                if ("stopServer".equalsIgnoreCase(operateType)) {
                    instanceStatusStr = HsConstants.STATUS_STOP;
                } else if ("delete".equalsIgnoreCase(operateType)) {
                    instanceStatusStr = HsConstants.STATUS_DELETE;
                }

                if (updateRet.getStatusCodeValue() != org.springframework.http.HttpStatus.OK.value()) {
                    setStatus = HsConstants.FAIL;
                    instanceStatusStr = HsConstants.STATUS_ERROR;
                }
                instance.setResult(setStatus);
                instance.setInstanceStatus(instanceStatusStr);
                instance.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                log.info("Soft down or delete result:{}", updateRet);
            }
            if (null != updateRet) {
                webControllerService.resultReturnNotify(softDown);
                return updateRet;
            }
        } catch (Exception e) {
            log.error("Exception in stopOrSoftDeleteSbw sbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage() + "";
        }
        webControllerService.resultReturnNotify(softDown);
        return new ResponseEntity<>(ReturnMsgUtil.error(code, msg),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * get eip config from order
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

        log.info("Get sbw param from sbw Recive:{}", updateParam.toString());
        return updateParam;
    }

    private SbwUpdateParam getSbwUpdatParmByOrder(ReciveOrder eipOrder) {
        SbwUpdateParam sbwParam = new SbwUpdateParam();
        List<OrderProduct> orderProducts = eipOrder.getProductList();
        sbwParam.setBillType(eipOrder.getBillType());
        sbwParam.setDuration(eipOrder.getDuration());
        for (OrderProduct orderProduct : orderProducts) {
            if (!orderProduct.getProductLineCode().equals(HsConstants.SBW)) {
                continue;
            }
            sbwParam.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for (OrderProductItem orderProductItem : orderProductItems) {
                if (orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)) {
                    sbwParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                }
            }
        }
        log.info("Get sbw param from order:{}", sbwParam.toString());
        /*chargemode now use the default value */
        return sbwParam;
    }

    private EipOrderResult getSbwResult(ReciveOrder reciveOrder, String sbwId, String result) {
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
