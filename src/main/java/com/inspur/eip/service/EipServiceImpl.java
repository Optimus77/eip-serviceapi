package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

            int left = getQuota();
            if(left > 0) {
                JSONObject eipAllocateParam = JSON.parseObject(eipAllocateJson);
                JSONObject eip = eipAllocateParam.getJSONObject("eip");

                EipOrder order = getOrderByEipParam(eip.getInteger("bandwidth"),
                        eip.getString("iptype"),
                        eip.getString("region"),
                        eip.getString("purchasetime"), null);

                order.setConsoleCustomization(eipAllocateParam);

                result = bssApiService.createOrder(order);
                log.info("create order result:{}", result.toString());
                return result;
            }else{
                result=new JSONObject();
                result.put("code","106.999500");
                result.put("msg", "quota limited, user can not create eip.");
            }
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
    public int getQuota(){
        JSONObject result;
        try{
            EipQuota quota=new EipQuota();
            quota.setProductLineCode("EIP");
            quota.setRegion(CommonUtil.getReginInfo());
            quota.setProductTypeCode("EIP");
            quota.setUserId(CommonUtil.getUserId());
            result =bssApiService.getQuota(quota);
            log.info("Get quota.result:{}", result.toJSONString());
            if(null!= result.getString("code") && result.getString("code").equals("0")){
                JSONArray qutoResult =result.getJSONObject("result").getJSONArray("quotaList");
                for(int i=0; i< qutoResult.size(); i++) {
                    JSONObject jb = qutoResult.getJSONObject(i);
                    if(jb.get("productLineCode").equals("EIP") ){
                        return Integer.valueOf(jb.getString("leftNumber"));
                    }
                }
            }
            log.error("Failed to get quota.result:{}", result.toJSONString());
        }catch (Exception e){
           log.error("Failed to get quota.result", e);
        }
        return 1;
    }


    private EipOrder getOrderByEipParam(int bandWidth, String ipType, String region, String purchasetime, String eipId) {

        List<EipOrderProductItem> itemList = new ArrayList<>();
        EipOrderProductItem bandWidthItem = new EipOrderProductItem();
        bandWidthItem.setCode("net");
        bandWidthItem.setName("带宽");
        bandWidthItem.setUnit("M");
        bandWidthItem.setValue(String.valueOf(bandWidth));
        bandWidthItem.setType("billingItem");

        EipOrderProductItem ipTypeItem = new EipOrderProductItem();
        ipTypeItem.setCode("provider");
        ipTypeItem.setName("BGP网络");
        ipTypeItem.setValue("BGP");
        ipTypeItem.setType("impactFactor");

        EipOrderProductItem chargeMode = new EipOrderProductItem();
        chargeMode.setCode("chargemode");
        chargeMode.setName("计费方式");
        chargeMode.setValue("Bandwidth");
        chargeMode.setType("billingItem");

        itemList.add(bandWidthItem);
        itemList.add(ipTypeItem);
        itemList.add(chargeMode);

        EipOrderProduct eipOrderProduct = new EipOrderProduct();
        eipOrderProduct.setItemList(itemList);
        eipOrderProduct.setRegion(region);
        eipOrderProduct.setAvailableZone("");
        eipOrderProduct.setInstanceId(eipId);
        eipOrderProduct.setInstanceId("");

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
