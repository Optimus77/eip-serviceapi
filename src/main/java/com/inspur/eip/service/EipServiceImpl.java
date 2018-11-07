package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.EipOrder;
import com.inspur.eip.entity.EipOrderProduct;
import com.inspur.eip.entity.EipOrderProductItem;
import com.inspur.eip.entity.EipQuota;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.ReturnMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EipServiceImpl  {

    @Autowired
    private BssApiService bssApiService;

    public final static Logger log = LoggerFactory.getLogger(EipServiceImpl.class);


    /**
     *  create a order
     * @param eipAllocateJson eip allocat json
     */
    public JSONObject createOrder(String eipAllocateJson) {

        JSONObject result;
        try{
            JSONObject eipAllocateParam = JSON.parseObject(eipAllocateJson);
            JSONObject eip = eipAllocateParam.getJSONObject("eip");

            EipOrder order = getOrderByEipParam(eip.getInteger("bandwidth"),
                    eip.getString("iptype"),
                    eip.getString("region"),
                    eip.getString("purchasetime"), null);

            order.setConsoleCustomization(eipAllocateParam);

            result=bssApiService.createOrder(order);
            log.info("create order result:{}",result.toString());
            return  result;
        }catch (Exception e){
            e.printStackTrace();
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

            EipOrder order = getOrderByEipParam(0, null, null, null, eipId);
            order.setOrderType("unsubscribe");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eipId", eipId);
            order.setConsoleCustomization(jsonObject);

            result=bssApiService.createOrder(order);
            log.info("delete eip order result:{}",result.toString());
            return  result;
        }catch (Exception e){
            e.printStackTrace();
            result=new JSONObject();
            result.put("code","106.999500");
            result.put("msg",e.getMessage());
        }
        return result;
    }



    //1.2.13	查询用户配额的接口
    public ResponseEntity getQuota(){
        try{
            EipQuota quota=new EipQuota();
            quota.setProductLineCode("EIP");
            quota.setRegion(CommonUtil.getReginInfo());
            quota.setProductTypeCode(null);
            quota.setUserId(CommonUtil.getUserId());
            JSONObject result=bssApiService.getQuota(quota);
            if(result.getBoolean("success")){
                return new ResponseEntity<>(HttpStatus.OK);
            }else{
                return new ResponseEntity<>(ReturnMsgUtil.error(400, "error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            return new ResponseEntity<>(ReturnMsgUtil.error(500,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    private EipOrder getOrderByEipParam(int bandWidth, String ipType, String region, String purchasetime, String eipId) {

        List<EipOrderProductItem> itemList = new ArrayList<>();
        EipOrderProductItem bandWidthItem = new EipOrderProductItem();
        bandWidthItem.setCode("net");
        bandWidthItem.setName("bandwidth");
        bandWidthItem.setUnit("M");
        bandWidthItem.setValue(String.valueOf(bandWidth));
        bandWidthItem.setType("billingItem");

        EipOrderProductItem ipTypeItem = new EipOrderProductItem();
        ipTypeItem.setCode("provider");
        ipTypeItem.setName(ipType);
        ipTypeItem.setValue("BGP");
        ipTypeItem.setType("impactFactor");

        EipOrderProductItem chargeMode = new EipOrderProductItem();
        ipTypeItem.setCode("chargemode");
        ipTypeItem.setName("chargemode");
        ipTypeItem.setValue("Bandwidth");
        ipTypeItem.setType("billingItem");

        itemList.add(bandWidthItem);
        itemList.add(ipTypeItem);
        itemList.add(chargeMode);

        EipOrderProduct eipOrderProduct = new EipOrderProduct();
        eipOrderProduct.setItemList(itemList);
        eipOrderProduct.setRegion(region);
        eipOrderProduct.setAvailableZone("");
        eipOrderProduct.setInstanceId(eipId);

        EipOrder eipOrder = new EipOrder();
        try {
            eipOrder.setUserId(CommonUtil.getUserId());
        }catch (Exception e){
            e.printStackTrace();
        }
        eipOrder.setToken(CommonUtil.getKeycloackToken());
        eipOrder.setConsoleOrderFlowId(UUID.randomUUID().toString());
        List<EipOrderProduct> orders = new ArrayList<>();
        orders.add(eipOrderProduct);
        eipOrder.setDuration(purchasetime);
        eipOrder.setProductList(orders);

        return eipOrder;
    }

}
