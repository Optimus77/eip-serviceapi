package com.inspur.eip.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.*;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.inspur.eip.util.CommonUtil.preCheckParam;

@Service
@Slf4j
public class BssApiService {

    @Autowired
    private EipAtomService eipAtomService;

    @Autowired
    private WebControllerService webControllerService;

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
    public JSONObject onReciveCreateOrderResult(EipReciveOrder eipOrder) {

        String code;
        String msg;
        String   eipId = "";
        JSONObject createRet = null;
        ReturnResult returnResult = null;
        try {
            log.debug("Recive create order:{}", JSONObject.toJSONString(eipOrder));
            EipOrder message =  eipOrder.getReturnConsoleMessage();
            if(eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS) ||
                    message.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
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
    public JSONObject onReciveDeleteOrderResult(EipReciveOrder eipOrder) {
        String msg ;
        String code ;
        String eipId = "0";
        try {
            log.debug("Recive delete order:{}", JSONObject.toJSONString(eipOrder));
            EipOrder message =  eipOrder.getReturnConsoleMessage();
            if(eipOrder.getOrderStatus().equals(HsConstants.CREATESUCCESS)  ||
                    message.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {

                EipAllocateParam eipConfig = getEipConfigByOrder(eipOrder);

                List<EipOrderProduct> eipOrderProducts = message.getProductList();
                for (EipOrderProduct eipOrderProduct : eipOrderProducts) {
                    eipId = eipOrderProduct.getInstanceId();
                }
                JSONObject delResult = eipAtomService.atomDeleteEip(eipId);

                if (delResult.getInteger(HsConstants.STATUSCODE) == org.springframework.http.HttpStatus.OK.value()) {
                    //Return message to the front des
                    webControllerService.returnsWebsocket(eipId, eipOrder, "delete");
                    webControllerService.resultReturnMq(getEipOrderResult(eipOrder, eipId, HsConstants.SUCCESS));
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
    public JSONObject onReciveUpdateOrder(String eipId, EipReciveOrder eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;

        try {
            log.debug("Recive update order:{}", JSONObject.toJSONString(eipOrder));
            EipOrder message =  eipOrder.getReturnConsoleMessage();

            if((null != message) && (eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS) ||
                    message.getBillType().equals(HsConstants.HOURLYSETTLEMENT))) {
                EipAllocateParam eipUpdate = getEipConfigByOrder(eipOrder);
                JSONObject updateRet;
                if(message.getOrderType().equalsIgnoreCase("changeConfigure")){
                    updateRet = eipAtomService.atomUpdateEip(eipId, eipUpdate);
                }else if(message.getOrderType().equalsIgnoreCase("renew")){
                    updateRet = eipAtomService.atomRenewEip(eipId, eipUpdate);
                }else{
                    log.error("Not support order type:{}", message.getOrderType());
                    updateRet = CommonUtil.handlerResopnse(null);
                }
                String retStr = HsConstants.SUCCESS;
                if (updateRet.getInteger(HsConstants.STATUSCODE) != HttpStatus.SC_OK){
                    retStr = HsConstants.FAIL;
                }

                log.info("renew order result :{}",updateRet);
                webControllerService.returnsWebsocket(eipId, eipOrder, "update");
                webControllerService.resultReturnMq(getEipOrderResult(eipOrder,"",retStr));
                return updateRet;
            }
        }catch (Exception e){
            log.error("Exception in update eip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        webControllerService.resultReturnMq(getEipOrderResult(eipOrder,eipId,HsConstants.FAIL));
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
    public JSONObject onReciveSoftDownOrder(EipSoftDownOrder eipOrder) {
        String msg = "";
        String code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
        JSONObject updateRet = null;
        try {
            log.debug("Recive soft down order:{}", JSONObject.toJSONString(eipOrder));
            List<EipSoftDownInstance> instanceList =  eipOrder.getInstanceList();
            for(EipSoftDownInstance eipSoftDownInstance: instanceList){
                String operateType =  eipSoftDownInstance.getOperateType();
                String instanceStatus;
                if("delete".equalsIgnoreCase(operateType)) {
                    updateRet = eipAtomService.atomDeleteEip(eipSoftDownInstance.getInstanceId());
                    instanceStatus = "DELETED";
                }else if("stopServer".equalsIgnoreCase(operateType)) {
                    EipAllocateParam updateParam = new EipAllocateParam();
                    updateParam.setDuration("0");
                    instanceStatus = "DOWN";
                    updateRet = eipAtomService.atomRenewEip(eipSoftDownInstance.getInstanceId(), updateParam);
                }else{
                    continue;
                }

                String retStr = HsConstants.SUCCESS;
                if (updateRet.getInteger(HsConstants.STATUSCODE) != org.springframework.http.HttpStatus.OK.value()){
                    retStr = HsConstants.FAIL;
                    instanceStatus = "ACTIVE";
                }
                eipSoftDownInstance.setResult(retStr);
                eipSoftDownInstance.setInstanceStatus(instanceStatus);
                eipSoftDownInstance.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
    private  EipAllocateParam getEipConfigByOrder(EipReciveOrder eipOrder){
        EipAllocateParam eipAllocateParam = new EipAllocateParam();
        eipAllocateParam.setDuration(eipOrder.getReturnConsoleMessage().getDuration());
        List<EipOrderProduct> eipOrderProducts = eipOrder.getReturnConsoleMessage().getProductList();

        eipAllocateParam.setBillType(eipOrder.getReturnConsoleMessage().getBillType());

        for(EipOrderProduct eipOrderProduct: eipOrderProducts){
            if(!eipOrderProduct.getProductLineCode().equals(HsConstants.EIP)){
                continue;
            }
            eipAllocateParam.setRegion(eipOrderProduct.getRegion());
            List<EipOrderProductItem> eipOrderProductItems = eipOrderProduct.getItemList();

            for(EipOrderProductItem eipOrderProductItem: eipOrderProductItems){
                if(eipOrderProductItem.getCode().equalsIgnoreCase("bandwidth") &&
                        eipOrderProductItem.getUnit().equals(HsConstants.M)){
                    eipAllocateParam.setBandwidth(Integer.parseInt(eipOrderProductItem.getValue()));
                }else if(eipOrderProductItem.getCode().equals(HsConstants.PROVIDER) &&
                        eipOrderProductItem.getType().equals(HsConstants.IMPACTFACTOR)){
                    eipAllocateParam.setIptype(eipOrderProductItem.getValue());
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
     * @param eipReciveOrder order
     * @param eipId id
     * @param result result
     * @return EipOrderResult
     */
    private   EipOrderResult getEipOrderResult(EipReciveOrder eipReciveOrder, String eipId, String result){
        EipOrder eipOrder = eipReciveOrder.getReturnConsoleMessage();
        List<EipOrderProduct> eipOrderProducts = eipOrder.getProductList();

        for(EipOrderProduct eipOrderProduct: eipOrderProducts){
            eipOrderProduct.setInstanceStatus(result);
            eipOrderProduct.setInstanceId(eipId);
            eipOrderProduct.setStatusTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }

        EipOrderResult eipOrderResult = new EipOrderResult();
        eipOrderResult.setUserId(eipOrder.getUserId());
        eipOrderResult.setConsoleOrderFlowId(eipReciveOrder.getConsoleOrderFlowId());
        eipOrderResult.setOrderId(eipReciveOrder.getOrderId());

        List<EipOrderResultProduct> eipOrderResultProducts = new ArrayList<>();
        EipOrderResultProduct eipOrderResultProduct = new EipOrderResultProduct();
        eipOrderResultProduct.setOrderDetailFlowId(eipReciveOrder.getOrderDetailFlowIdList().get(0));
        eipOrderResultProduct.setProductSetStatus(result);
        eipOrderResultProduct.setBillType(eipOrder.getBillType());
        eipOrderResultProduct.setDuration(eipOrder.getDuration());
        eipOrderResultProduct.setOrderType(eipOrder.getOrderType());
        eipOrderResultProduct.setProductList(eipOrder.getProductList());


        eipOrderResultProducts.add(eipOrderResultProduct);
        eipOrderResult.setProductSetList(eipOrderResultProducts);
        return eipOrderResult;
    }
}
