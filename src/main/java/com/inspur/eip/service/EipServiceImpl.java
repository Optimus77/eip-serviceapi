package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.inspur.eip.entity.*;
import com.inspur.eip.util.*;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class EipServiceImpl  {

    @Autowired
    private BssApiService bssApiService;

    public final static Logger log = LoggerFactory.getLogger(EipServiceImpl.class);


    //1.2.8 订单接口POST
    @Value("${bssURL.eipAtom}")
    private   String eipAtomUrl;
    private JSONObject atomCreateEip(EipAllocateParam eipConfig)  {
        String url=eipAtomUrl;

        String orderStr=JSONObject.toJSONString(eipConfig);
        log.info("Send order to url:{}, body:{}",url, orderStr);

        HttpResponse response=HttpUtil.post(url,null,orderStr);
        return CommonUtil.handlerResopnse(response);
    }
    private JSONObject atomDeleteEip(String  eipId)  {
        String url=eipAtomUrl+eipId;

        log.info("Send order to url:{}, eipId:{}",url, eipId);

        HttpResponse response=HttpUtil.delete(url,null);
        return CommonUtil.handlerResopnse(response);
    }

    /**
     *  create a order
     * @param eipAllocateJson eip allocat json
     */
    public JSONObject createOrder(String eipAllocateJson) {

        JSONObject result;
        try{

            int left = getQuota();
            if(left > 0) {
                JSONObject eipAllocateParam = JSON.parseObject(eipAllocateJson);
                JSONObject eip = eipAllocateParam.getJSONObject("eip");

                EipOrder order = getOrderByEipParam(eip.getInteger(HsConstants.BANDWIDTH),
                        eip.getString(HsConstants.IPTYPE),
                        eip.getString(HsConstants.REGION),
                        eip.getString(HsConstants.DURATION), 
                        eip.getString("billType"),
                        "");

                order.setConsoleCustomization(eipAllocateParam);

                result = bssApiService.createOrder(order);
                log.info("Send create order result:{}", result);
                return result;
            }else{
                result=new JSONObject();
                result.put("code","106.999500");
                result.put("msg", "quota limited, user can not create eip.");
            }
        }catch (Exception e){
            log.info("createOrder exception", e);
            result=new JSONObject();
            result.put("code","106.999500");
            result.put("msg",e.getMessage());
        }
        return  result;
    }

    /**
     * delete eip
     * @param eipId eipid
     * @return return
     */
    public JSONObject deleteEipOrder(String eipId) {
        JSONObject result;
        try{
            JSONObject eipEntity = getEipEntityById(eipId);
            JSONObject eip = eipEntity.getJSONObject("eip");
            String region = eip.getString("region");
            Integer bandwidth = eip.getInteger("bandwidth");
            String duration = eip.getString("duration");
            String ipType = eip.getString("iptype");
            String billType = eip.getString("billType");

            EipOrder order = getOrderByEipParam(bandwidth, ipType, "cn-north-3", duration,billType, eipId);
            order.setOrderType(HsConstants.UNSUBSCRIBE);
            order.setBillType("hourlySettlement");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eipId", eipId);
            order.setConsoleCustomization(jsonObject);

            result=bssApiService.createOrder(order);
            log.info("delete eip order result:{}",result);
            return  result;
        }catch (Exception e){
            log.error("Exception when deleteEipOrder", e);
            result=new JSONObject();
            result.put("code","106.999500");
            result.put("msg",e.getMessage());
        }
        return result;
    }


    public JSONObject onReciveOrderResult(EipReciveOrder eipOrder) {

        String code;
        String msg;
        try {
            log.info("Recive order:{}", JSONObject.toJSONString(eipOrder));
            EipOrder retrunMsg =  eipOrder.getReturnConsoleMessage();
            if(eipOrder.getOrderStatus().equals(HsConstants.PAYSUCCESS) ||
                    retrunMsg.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                EipAllocateParam eipConfig = getEipConfigByOrder(eipOrder);
                ReturnMsg returnMsg = preCheckParam(eipConfig);
                if(returnMsg.getCode().equals(ReturnStatus.SC_OK)){
                    //post request to atom
                    JSONObject result = atomCreateEip(eipConfig);
                    //if(result.get)
                    bssApiService.resultReturnMq(getEipOrderResult(eipOrder, "",HsConstants.FAIL));
                    return result;
                } else {
                    code = ReturnStatus.SC_OPENSTACK_FIPCREATE_ERROR;
                    msg = "Failed to create floating ip in external network:" + eipConfig.getRegion();
                    log.error(msg);
                }
            }else {
                bssApiService.resultReturnMq(getEipOrderResult(eipOrder, "",HsConstants.FAIL));
                code = ReturnStatus.SC_RESOURCE_ERROR;
                msg = "not payed.";
                log.info(msg);
            }
        }catch (Exception e){
            log.error("Exception in createEip", e);
            code = ReturnStatus.SC_INTERNAL_SERVER_ERROR;
            msg = e.getMessage()+"";
        }
        bssApiService.resultReturnMq(getEipOrderResult(eipOrder, "",HsConstants.FAIL));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
    public JSONObject onReciveDeleteOrderResult( EipReciveOrder eipOrder) {
        String msg;
        String code;
        String eipId = "0";
        try {
            EipOrder retrunMsg =  eipOrder.getReturnConsoleMessage();
            if(eipOrder.getOrderStatus().equals("createSuccess")  ||
                    retrunMsg.getBillType().equals(HsConstants.HOURLYSETTLEMENT)) {
                //Todo: find the eipid
                List<EipOrderProduct> eipOrderProducts = retrunMsg.getProductList();
                for(EipOrderProduct eipOrderProduct: eipOrderProducts){
                    eipId = eipOrderProduct.getInstanceId();
                }
                JSONObject delResult = atomDeleteEip(eipId);

                if (delResult.getInteger("statusCode") != HttpStatus.OK.value()){


                    //Return message to the front des
                    //returnsWebsocket(eipId,eipOrder,"delete");

                    bssApiService.resultReturnMq(getEipOrderResult(eipOrder, eipId,"success"));
                    return delResult;
                }else {
                    msg = delResult.getString("statusCode");
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
        bssApiService.resultReturnMq(getEipOrderResult(eipOrder, eipId,HsConstants.FAIL));
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
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
                if(eipOrderProductItem.getCode().equals("bandwidth") &&
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
    private ReturnMsg preCheckParam(EipAllocateParam param){
        String errorMsg = "success";
        if(param.getBandwidth() > 2000){
            errorMsg = "value must be 1-2000.";
        }
        if(!param.getChargemode().equals(HsConstants.BANDWIDTH) &&
                !param.getChargemode().equals(HsConstants.SHAREDBANDWIDTH)){
            errorMsg = errorMsg + "Only Bandwidth,SharedBandwidth is allowed. ";
        }

        if(!param.getBillType().equals(HsConstants.MONTHLY) && !param.getBillType().equals(HsConstants.HOURLYSETTLEMENT)){
            errorMsg = errorMsg + "Only monthly,hourlySettlement is allowed. ";
        }
        if(param.getRegion().isEmpty()){
            errorMsg = errorMsg + "can not be blank.";
        }
        String tp = param.getIptype();
        if(!tp.equals("5_bgp") && !tp.equals("5_sbgp") && !tp.equals("5_telcom") &&
                !tp.equals("5_union") && !tp.equals("BGP")){
            errorMsg = errorMsg +"Only 5_bgp,5_sbgp, 5_telcom, 5_union ,  BGP is allowed. ";
        }
        if(errorMsg.equals("success")) {
            log.info(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_OK, errorMsg);
        }else {
            log.error(errorMsg);
            return ReturnMsgUtil.error(ReturnStatus.SC_PARAM_ERROR,errorMsg);
        }
    }


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
    //1.2.13	查询用户配额的接口
    public int getQuota(){
        JSONObject result;
        try{
            EipQuota quota=new EipQuota();
            quota.setProductLineCode(HsConstants.EIP);
            quota.setRegion(CommonUtil.getReginInfo());
            quota.setProductTypeCode(HsConstants.EIP);
            quota.setUserId(CommonUtil.getUserId());

            result =bssApiService.getQuota(quota);
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


    private EipOrder getOrderByEipParam(int bandWidth, String ipType, String region, String duration, String billType, String eipId) {

        List<EipOrderProductItem> itemList = new ArrayList<>();
        EipOrderProductItem bandWidthItem = new EipOrderProductItem();
        bandWidthItem.setCode(HsConstants.BANDWIDTH);
        bandWidthItem.setName("带宽");
        bandWidthItem.setUnit(HsConstants.M);
        bandWidthItem.setValue(String.valueOf(bandWidth));
        bandWidthItem.setType(HsConstants.BILLINGITEM);

        EipOrderProductItem ipTypeItem = new EipOrderProductItem();
        ipTypeItem.setCode(HsConstants.PROVIDER);
        ipTypeItem.setName("线路");
        ipTypeItem.setValue(HsConstants.BGP);
        ipTypeItem.setUnit("");
        ipTypeItem.setType(HsConstants.IMPACTFACTOR);

        EipOrderProductItem trasfer = new EipOrderProductItem();
        trasfer.setCode(HsConstants.TRANSFER);
        trasfer.setName("流量");
        trasfer.setUnit("Gb");
        trasfer.setValue("0");
        trasfer.setType(HsConstants.BILLINGITEM);

        EipOrderProductItem ip = new EipOrderProductItem();
        ip.setCode("IP");
        ip.setName("IP费用");
        ip.setValue("1");
        ip.setType(HsConstants.BILLINGITEM);
        ip.setUnit("个");

        itemList.add(bandWidthItem);
        itemList.add(ipTypeItem);
        itemList.add(ip);
        itemList.add(trasfer);

        EipOrderProduct eipOrderProduct = new EipOrderProduct();
        eipOrderProduct.setItemList(itemList);
        eipOrderProduct.setRegion(region);
        eipOrderProduct.setAvailableZone("");
        eipOrderProduct.setInstanceId(eipId);

        EipOrder eipOrder = new EipOrder();
        try {
            eipOrder.setUserId(CommonUtil.getUserId());
        }catch (Exception e){
            log.error("Exception when get user id in getOrderByEipParam.", e);
        }
        String bearerToken = CommonUtil.getKeycloackToken();
        if(bearerToken.startsWith("Bearer ")){
            bearerToken = bearerToken.split(" ")[1];
        }
        eipOrder.setToken(bearerToken);
        eipOrder.setConsoleOrderFlowId(UUID.randomUUID().toString());
        List<EipOrderProduct> orders = new ArrayList<>();
        orders.add(eipOrderProduct);
        eipOrder.setDuration(duration);
        eipOrder.setBillType(billType);
        eipOrder.setProductList(orders);

        return eipOrder;
    }

    private JSONObject getEipEntityById(String eipId){

        String  uri =eipAtomUrl+eipId;
        log.info(uri);
        HttpResponse response= HttpUtil.get(uri,null);
        return  CommonUtil.handlerResopnse(response);
    }

}
