package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.EipOrder;
import com.inspur.eip.entity.EipOrderProduct;
import com.inspur.eip.entity.EipOrderProductItem;
import com.inspur.eip.entity.EipQuota;
import com.inspur.eip.util.CommonUtil;
import com.inspur.eip.util.HsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

                EipOrder order = getOrderByEipParam(eip.getInteger(HsConstants.BANDWIDTH),
                        eip.getString(HsConstants.IPTYPE),
                        eip.getString(HsConstants.REGION),
                        eip.getString(HsConstants.DURATION), "");

                order.setConsoleCustomization(eipAllocateParam);

                result = bssApiService.createOrder(order);
                log.info("create order result:{}", result);
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

            EipOrder order = getOrderByEipParam(1, "", "", "", eipId);
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
            if(null!= result.getBoolean(HsConstants.SUCCESS) && result.getBoolean(HsConstants.SUCCESS)){
                JSONArray qutoResult =result.getJSONObject("data").getJSONArray("quotaList");
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


    private EipOrder getOrderByEipParam(int bandWidth, String ipType, String region, String duration, String eipId) {

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
        eipOrder.setProductList(orders);

        return eipOrder;
    }

}
