package com.inspur.eip.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.*;
import com.inspur.eip.entity.sbw.*;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.inspur.eip.util.CommonUtil.preCheckParam;
import static com.inspur.eip.util.CommonUtil.preSbwCheckParam;

@Service
@Slf4j
public class BssApiService {

    @Autowired
    private EipAtomService eipAtomService;

    @Autowired
    private WebControllerService webControllerService;

    @Autowired
    private SbwAtomService sbwAtomService;

    //1.2.11	查询用户配额的接口 URL: http://117.73.2.105:8083/crm/quota
    @Value("${bssurl.quotaUrl}")
    private   String quotaUrl;

    /**
     * get quota
     * @param quota quota
     * @return string
     */
    private ReturnResult getQuota(EipQuota quota){
        try {
            String uri = quotaUrl + "?userId=" + quota.getUserId() + "&region=" + quota.getRegion() + "&productLineCode="
                    + quota.getProductLineCode() + "&productTypeCode=" + quota.getProductTypeCode() + "&quotaType=amount";
            log.info("Get quota: {}", uri);

            ReturnResult response;
            if((quotaUrl.startsWith("https://")) ||(quotaUrl.startsWith("HTTPS://"))){
                Map<String,String> header=new HashMap<>();
                header.put(HsConstants.AUTHORIZATION, CommonUtil.getKeycloackToken());
                response = HttpsClientUtil.doGet(uri, header);
            }else{
                response = HttpUtil.get(uri, null);
            }
            return response;
        }catch (Exception e){
            log.error("In quota query, get token exception:{}", e);
        }
        return ReturnResult.actionFailed("Quota query failed ", HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * get create order result
     * @param eipOrder order
     * @return return message
     */
    public JSONObject onReciveCreateOrderResult(ReciveOrder eipOrder) {

        String code;
        String msg;
        String   eipId = "";
        JSONObject createRet = null;
        ReturnResult returnResult = null;
        try {
            log.debug("Recive create order:{}", JSONObject.toJSONString(eipOrder));
            if(eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                EipAllocateParam eipConfig = getEipConfigByOrder(eipOrder);
                ReturnMsg checkRet = preCheckParam(eipConfig);
                if(checkRet.getCode().equals(ReturnStatus.SC_OK)){
                    //post request to atom
                    EipAllocateParamWrapper eipAllocateParamWrapper = new EipAllocateParamWrapper();
                    eipAllocateParamWrapper.setEip(eipConfig);
                    createRet = eipAtomService.atomCreateEip(eipAllocateParamWrapper);
                    String retStr = HsConstants.SUCCESS;

                    if(createRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                        retStr = HsConstants.FAIL;
                        log.info("create eip failed, return code:{}", createRet.getInteger(HsConstants.STATUSCODE));
                    }else{
                        JSONObject eipEntity = createRet.getJSONObject("eip");
                        eipId = eipEntity.getString("eipid");
                        webControllerService.returnsWebsocket(eipEntity.getString("eipid"),eipOrder,"create");
                        if(eipConfig.getIpv6().equalsIgnoreCase("yes")){
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
            }else {
                code = ReturnStatus.SC_RESOURCE_ERROR;
                msg = "not payed.";
                log.info(msg);
            }
        }catch (Exception e){
            log.error("Exception in createEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }finally {
            if((null == returnResult) || (!returnResult.isSuccess())) {
                if ((null != createRet) && (createRet.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK)) {
                    log.error("Delete the allocate eip just now for mq message error, id:{}", eipId);
                    eipAtomService.atomDeleteEip(eipId);
                }
            }
        }
        webControllerService.resultReturnMq(getEipOrderResult(eipOrder, "",HsConstants.FAIL));
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
    public JSONObject onReciveDeleteOrderResult(ReciveOrder eipOrder) {
        String msg ;
        String code ;
        String eipId = "0";
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(eipOrder));
            if(eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {

                List<OrderProduct> orderProducts = eipOrder.getProductList();
                for (OrderProduct orderProduct : orderProducts) {
                    eipId = orderProduct.getInstanceId();
                }
                JSONObject delResult = eipAtomService.atomDeleteEip(eipId);

                if (delResult.getInteger(HsConstants.STATUSCODE) == org.springframework.http.HttpStatus.OK.value()) {
                    if(eipOrder.getConsoleCustomization().containsKey("operateType")
                            && eipOrder.getConsoleCustomization().getString("operateType").equalsIgnoreCase("deleteNatWithEip")){
                        webControllerService.returnsIpv6Websocket("Success", "Success", "deleteNatWithEip");
                    }else{
                        webControllerService.returnsWebsocket(eipId, eipOrder, "delete");
                    }
                    webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.UNSUBSCRIBE));
                    return delResult;
                } else {
                    msg = delResult.getString(HsConstants.STATUSCODE);
                    code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                }
            }else{
                msg = "Failed to delete eip,failed to create delete. orderStatus: "+eipOrder.getOrderStatus();
                code = ReturnStatus.SC_PARAM_UNKONWERROR;
                log.error(msg);
            }
        }catch (Exception e){
            log.error("Exception in deleteEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId,HsConstants.FAIL));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    /**
     * update order from bss
     * @param eipId id
     * @param eipOrder order
     * @return string
     */
    public JSONObject  onReciveUpdateOrder(String eipId, ReciveOrder eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;

        try {
            log.debug("Recive update order:{}", JSONObject.toJSONString(eipOrder));

            if((null != eipOrder) && (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS))) {
                EipUpdateParam eipUpdate = getUpdatParmByOrder(eipOrder);
                JSONObject updateRet;
                if(eipOrder.getOrderType().equalsIgnoreCase("changeConfigure")){
                    updateRet = eipAtomService.atomUpdateEip(eipId, eipUpdate);
                }else if(eipOrder.getOrderType().equalsIgnoreCase("renew") && eipOrder.getBillType().equals(HsConstants.MONTHLY)){
                    updateRet = eipAtomService.atomRenewEip(eipId, eipUpdate);
                }else{
                    log.error("Not support order type:{}", eipOrder.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                String retStr = HsConstants.SUCCESS;
                if (updateRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK){
                    retStr = HsConstants.FAIL;
                }

                log.info("renew order result :{}",updateRet);
                webControllerService.returnsWebsocket(eipId, eipOrder, "update");
                webControllerService.resultReturnMq(getEipOrderResult(eipOrder,eipId,retStr));
                return updateRet;
            }
        }catch (Exception e){
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        if(null != eipOrder) {
            webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.FAIL));
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
    public JSONObject onReciveSoftDownOrder(OrderSoftDown eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        JSONObject updateRet = null;
        String retStr = HsConstants.SUCCESS;
        String iStatusStr;
        try {
            log.debug("Recive soft down order:{}", JSONObject.toJSONString(eipOrder));
            List<SoftDownInstance> instanceList =  eipOrder.getInstanceList();
            for(SoftDownInstance softDownInstance : instanceList){
                String operateType =  softDownInstance.getOperateType();
                if("delete".equalsIgnoreCase(operateType)) {
                    updateRet = eipAtomService.atomDeleteEip(softDownInstance.getInstanceId());
                    iStatusStr = HsConstants.DELETED;
                }else if(HsConstants.STOPSERVER.equalsIgnoreCase(operateType)) {
                    EipUpdateParam updateParam = new EipUpdateParam();
                    updateParam.setDuration("0");
                    updateRet = eipAtomService.atomRenewEip(softDownInstance.getInstanceId(), updateParam);
                    iStatusStr = HsConstants.STOPSERVER;
                }else{
                    continue;
                }
                if (updateRet.getInteger(HsConstants.STATUSCODE) != org.springframework.http.HttpStatus.OK.value()){
                    retStr = HsConstants.FAIL;
                    iStatusStr = HsConstants.FAIL;
                }
                softDownInstance.setResult(retStr);
                softDownInstance.setInstanceStatus(iStatusStr);
                softDownInstance.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                log.info("Soft down result:{}", updateRet);
            }
            if(null != updateRet) {
                webControllerService.resultReturnNotify(eipOrder);
                return updateRet;
            }
        }catch (Exception e){
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultReturnNotify(eipOrder);
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * get eip config from order
     * @param eipOrder order
     * @return eip param
     */
    private  EipAllocateParam getEipConfigByOrder(ReciveOrder eipOrder){
        EipAllocateParam eipAllocateParam = new EipAllocateParam();
        List<OrderProduct> orderProducts = eipOrder.getProductList();

        eipAllocateParam.setBillType(eipOrder.getBillType());
        eipAllocateParam.setChargemode("Bandwidth");

        for(OrderProduct orderProduct : orderProducts){
            if(!orderProduct.getProductLineCode().equals(HsConstants.EIP)){
                continue;
            }
            eipAllocateParam.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for(OrderProductItem orderProductItem : orderProductItems){
                if(orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)){
                    eipAllocateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                }else if(orderProductItem.getCode().equals(HsConstants.PROVIDER)){
                    eipAllocateParam.setIptype(orderProductItem.getValue());
                }else if(orderProductItem.getCode().equals(HsConstants.IS_SBW) &&
                        orderProductItem.getValue().equals(HsConstants.YES)){
                    String sbwId = eipOrder.getConsoleCustomization().getString("sbwid");
                    eipAllocateParam.setSharedBandWidthId(sbwId);
                    eipAllocateParam.setChargemode("SharedBandwidth");
                }else if(orderProductItem.getCode().equals(HsConstants.WITH_IPV6) &&
                        orderProductItem.getValue().equals(HsConstants.YES)){
                    eipAllocateParam.setIpv6("yes");
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
    private  EipUpdateParam getUpdatParmByOrder(ReciveOrder eipOrder){
        EipUpdateParam eipAllocateParam = new EipUpdateParam();

        List<OrderProduct> orderProducts = eipOrder.getProductList();
        eipAllocateParam.setBillType(eipOrder.getBillType());
        eipAllocateParam.setChargemode("Bandwidth");
        eipAllocateParam.setDuration(eipOrder.getDuration());
        for(OrderProduct orderProduct : orderProducts){
            if(!orderProduct.getProductLineCode().equals(HsConstants.EIP)){
                continue;
            }
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for(OrderProductItem orderProductItem : orderProductItems){
                if(orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)){
                    eipAllocateParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                }else if(orderProductItem.getCode().equals(HsConstants.IS_SBW) ){
                    String sbwId = eipOrder.getConsoleCustomization().getString("sbwid");
                    eipAllocateParam.setSharedBandWidthId(sbwId);
                    if(orderProductItem.getValue().equalsIgnoreCase("yes")){
                        eipAllocateParam.setChargemode("SharedBandwidth");
                    }
                }
            }
        }
        log.info("Get eip param from order:{}", eipAllocateParam.toString());
        /*chargemode now use the default value */
        return eipAllocateParam;
    }


    /**
     * 查询用户配额的接口
     * @return int
     */
    int getQuotaResult(){
        ReturnResult retQuota;
        try{
            EipQuota quota=new EipQuota();
            quota.setProductLineCode(HsConstants.EIP);
            quota.setRegion(CommonUtil.getReginInfo());
            quota.setProductTypeCode(HsConstants.EIP);
            quota.setUserId(CommonUtil.getUserId());

            retQuota =getQuota(quota);
            if(retQuota.getCode() != org.springframework.http.HttpStatus.OK.value()){
                log.info("Get quota failed StatusCode:{}", retQuota.getCode());
            }
            JSONObject result = JSONObject.parseObject(retQuota.getMessage());
            if(null!= result.getString("code") && result.getString("code").equals("0")){
                JSONArray qutoResult =result.getJSONObject("result").getJSONArray("quotaList");
                for(int i=0; i< qutoResult.size(); i++) {
                    JSONObject jb = qutoResult.getJSONObject(i);
                    if(jb.get("productLineCode").equals("EIP") ){
                        log.info("Get quota success, number:{}", jb.getString("leftNumber"));
                        return Integer.valueOf(jb.getString("leftNumber"));
                    }
                }
            }
            log.error("Failed to get quota.result:{}", result.toJSONString());
        }catch (Exception e){
            log.error("Failed to get quota.result", e);
        }
        return 0;
    }

    /**
     * constructe EipOrderResult form order
     * @param reciveOrder order
     * @param eipId id
     * @param result result
     * @return EipOrderResult
     */
    private EipOrderResult getEipOrderResult(ReciveOrder reciveOrder, String eipId, String result){
        List<OrderProduct> orderProducts = reciveOrder.getProductList();

        for(OrderProduct orderProduct : orderProducts){
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
        if(HsConstants.FAIL.equalsIgnoreCase(result)){
            orderResultProduct.setProductSetStatus(result);
        }else {
            orderResultProduct.setProductSetStatus(HsConstants.SUCCESS);
        }
        orderResultProduct.setDuration(orderResultProduct.getDuration());
        orderResultProduct.setDurationUnit(orderResultProduct.getDurationUnit());
        orderResultProduct.setProductList(reciveOrder.getProductList());


        orderResultProducts.add(orderResultProduct);
        eipOrderResult.setProductSetList(orderResultProducts);
        return eipOrderResult;
    }
    /**
     * get create shareband result
     * @return return message
     */
    public JSONObject createShareBandWidth(ReciveOrder reciveOrder) {

        String code;
        String msg;
        String   sbwId = "";
        JSONObject createRet = null;
        ReturnResult returnResult = null;
        try {
            if(reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS) ) {
                SbwAtomParam sbwConfig = getSbwConfigByOrder(reciveOrder);
                ReturnSbwMsg checkRet = preSbwCheckParam(sbwConfig);
                if(checkRet.getCode().equals(ReturnStatus.SC_OK)){
                    //post request to atom
                    SbwAtomParamWrapper sbwWrapper = new SbwAtomParamWrapper();
                    sbwWrapper.setSbw(sbwConfig);
                    createRet = sbwAtomService.atomCreateSbw(sbwWrapper);
                    String retStr = HsConstants.STATUS_ACTIVE;

                    if(createRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK) {
                        retStr = HsConstants.STATUS_ERROR;
                        log.info("create sbw failed, return code:{}", createRet.getInteger(HsConstants.STATUSCODE));
                    }else{
                        JSONObject sbwEntity = createRet.getJSONObject("sbw");
                        sbwId = sbwEntity.getString("sbwid");
                        webControllerService.returnSbwWebsocket(sbwEntity.getString("sbwid"), reciveOrder,"create");
                    }
                    returnResult = webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId, retStr));

                    return createRet;
                } else {
                    code = ReturnStatus.SC_PARAM_ERROR;
                    msg = checkRet.getMessage();
                    log.error(msg);
                }
            }else {
                code = ReturnStatus.SC_RESOURCE_ERROR;
                msg = "not payed.";
                log.info(msg);
            }
        }catch (Exception e){
            log.error("Exception in createSbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }finally {
            if((null == returnResult) || (!returnResult.isSuccess())) {
                if ((null != createRet) && (createRet.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK)) {
                    log.error("Delete the allocate sbw just now for mq message error, id:{}", sbwId);
                    sbwAtomService.atomDeleteSbw(sbwId);
                }
            }
        }
        webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, "",HsConstants.STATUS_ERROR));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * delete result from bss
     * @param reciveOrder order
     * @return string
     */
    public JSONObject deleteShareBandWidth(ReciveOrder reciveOrder) {
        String msg ;
        String code ;
        String sbwId = "";
        JSONObject result = new JSONObject();
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(reciveOrder));
            if(reciveOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS) ) {

                List<OrderProduct> productList = reciveOrder.getProductList();
                for (OrderProduct product : productList) {
                    sbwId = product.getInstanceId();
                }
                JSONObject delResult = sbwAtomService.atomDeleteSbw(sbwId);

                if (delResult.getInteger(HsConstants.STATUSCODE) == HttpStatus.SC_OK) {
                    //Return message to the front des
                    webControllerService.returnSbwWebsocket(sbwId, reciveOrder, "delete");
                    webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId, HsConstants.STATUS_DELETE));
                    return delResult;
                } else {
                    msg = delResult.getString(HsConstants.STATUSCODE);
                    code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
                }
            }else{
                msg = "Failed to delete SBW,failed to create delete. orderStatus: "+ reciveOrder.getOrderStatus();
                code = ReturnStatus.SC_PARAM_UNKONWERROR;
                log.error(msg);
            }
        }catch (Exception e){
            log.error("Exception in deleteEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultSbwReturnMq(getSbwResult(reciveOrder, sbwId,HsConstants.STATUS_ERROR));
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * update the sbw config,incloud bandWidth and eip
     * @param sbwId id
     * @param recive info recived
     * @return ret
     */
    public JSONObject updateSbwConfig(String sbwId , ReciveOrder recive){
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;

        try {
            log.info("Recive update sbw:{}", JSONObject.toJSONString(recive));
            if(recive.getOrderStatus().equals(HsConstants.PAYSUCCESS)) {
                SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                SbwUpdateParam sbwUpdate = getSbwUpdatParmByOrder(recive);
                wrapper.setSbw(sbwUpdate);
                JSONObject updateRet;
                if(recive.getOrderType().equalsIgnoreCase("changeConfigure")){
                    updateRet = sbwAtomService.atomUpdateSbw(sbwId, wrapper);
                }else if(recive.getOrderType().equalsIgnoreCase("renew") && recive.getBillType().equals(HsConstants.MONTHLY)){
                    updateRet = sbwAtomService.atomRenewSbw(sbwId, wrapper);
                }else{
                    log.error("Not support order type:{}", recive.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                String retStr = HsConstants.STATUS_ACTIVE;
                if (updateRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK){
                   retStr = HsConstants.STATUS_ERROR;
                }

                log.info("update order result :{}",updateRet);
                webControllerService.returnSbwWebsocket(sbwId, recive, "update");
                webControllerService.resultSbwReturnMq(getSbwResult(recive, sbwId, retStr));
                return updateRet;
            }
        }catch (Exception e){
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultSbwReturnMq(getSbwResult(recive,sbwId,HsConstants.STATUS_ACTIVE));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public JSONObject stopOrSoftDeleteSbw(OrderSoftDown softDown) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        JSONObject updateRet = null;
        String setStatus = HsConstants.SUCCESS;;
        String instanceStatusStr ="";
        try {
            log.debug("Recive soft down or delete order:{}", JSONObject.toJSONString(softDown));
            List<SoftDownInstance> instanceList =  softDown.getInstanceList();
            for(SoftDownInstance instance : instanceList){
                String operateType =  instance.getOperateType();
                if("delete".equalsIgnoreCase(operateType)) {
                    updateRet = sbwAtomService.atomDeleteSbw(instance.getInstanceId());
                }else if("stopServer".equalsIgnoreCase(operateType)) {
                    SbwUpdateParamWrapper wrapper = new SbwUpdateParamWrapper();
                    SbwUpdateParam updateParam = new SbwUpdateParam();
                    updateParam.setDuration("0");
                    wrapper.setSbw(updateParam);
                    updateRet = sbwAtomService.atomRenewSbw(instance.getInstanceId(), wrapper);
                }else{
                    continue;
                }

                if ("stopServer".equalsIgnoreCase(operateType)){
                    instanceStatusStr = HsConstants.STATUS_STOP;
                }else if ("delete".equalsIgnoreCase(operateType)){
                    instanceStatusStr = HsConstants.STATUS_DELETE;
                }

                if (updateRet.getInteger(HsConstants.STATUSCODE) != org.springframework.http.HttpStatus.OK.value()){
                    setStatus = HsConstants.FAIL;
                    instanceStatusStr = HsConstants.STATUS_ERROR;
                }
                instance.setResult(setStatus);
                instance.setInstanceStatus(instanceStatusStr);
                instance.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                log.info("Soft down or delete result:{}", updateRet);
            }
            if(null != updateRet) {
                webControllerService.resultReturnNotify(softDown);
                return updateRet;
            }
        }catch (Exception e){
            log.error("Exception in stopOrSoftDeleteSbw sbw", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultReturnNotify(softDown);
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * get eip config from order
     * @return eip param
     */
    private SbwAtomParam getSbwConfigByOrder(ReciveOrder reciveOrder){
        SbwAtomParam sbwAllocatePram = new SbwAtomParam();
        JSONObject customization = reciveOrder.getConsoleCustomization();
        sbwAllocatePram.setBillType(reciveOrder.getBillType());
        sbwAllocatePram.setSbwName(customization.getString("sharedbandwidthname"));
        sbwAllocatePram.setDuration(reciveOrder.getDuration());
        List<OrderProduct> productList = reciveOrder.getProductList();
        for(OrderProduct orderProduct : productList){
            if(!orderProduct.getProductLineCode().equalsIgnoreCase(HsConstants.SBW)){
                continue;
            }
            sbwAllocatePram.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItemList = orderProduct.getItemList();

            for(OrderProductItem sbwItem: orderProductItemList){
                if(sbwItem.getCode().equalsIgnoreCase("bandwidth")){
                    sbwAllocatePram.setBandwidth(Integer.parseInt(sbwItem.getValue()));
                }
            }
        }

        log.info("Get sbw param from sbw Recive:{}", sbwAllocatePram.toString());
        return sbwAllocatePram;
    }

    private  SbwUpdateParam getSbwUpdatParmByOrder(ReciveOrder eipOrder){
        SbwUpdateParam sbwParam = new SbwUpdateParam();

        List<OrderProduct> orderProducts = eipOrder.getProductList();
        sbwParam.setBillType(eipOrder.getBillType());
        sbwParam.setDuration(eipOrder.getDuration());
        for(OrderProduct orderProduct : orderProducts){
            if(!orderProduct.getProductLineCode().equals(HsConstants.SBW)){
                continue;
            }
            sbwParam.setRegion(orderProduct.getRegion());
            List<OrderProductItem> orderProductItems = orderProduct.getItemList();

            for(OrderProductItem orderProductItem : orderProductItems){
                if(orderProductItem.getCode().equalsIgnoreCase(HsConstants.BANDWIDTH)){
                    sbwParam.setBandwidth(Integer.parseInt(orderProductItem.getValue()));
                }
            }
        }
        log.info("Get eip param from order:{}", sbwParam.toString());
        /*chargemode now use the default value */
        return sbwParam;
    }

    private EipOrderResult getSbwResult(ReciveOrder reciveOrder, String sbwId, String result){
        List<OrderProduct> productList = reciveOrder.getProductList();

        for(OrderProduct orderProduct : productList){
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
        if (HsConstants.STATUS_ERROR.equalsIgnoreCase(result)){
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
