package com.inspur.eip.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.inspur.eip.entity.*;
import com.inspur.eip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.inspur.eip.util.CommonUtil.preCheckParam;


@Service
@Slf4j
public class EipServiceImpl  {

    @Autowired
    private BssApiService bssApiService;

    @Autowired
    private EipAtomService eipAtomService;

    @Autowired
    private WebControllerService webControllerService;

    /**
     *  create a order
     * @param eipAllocateJson eip allocat json
     */
    public String createOrder(String eipAllocateJson) {

        try{
            int left = bssApiService.getQuotaResult();
            if(left > 0) {
                JSONObject eipAllocateParam = JSON.parseObject(eipAllocateJson);
                JSONObject eip = eipAllocateParam.getJSONObject("eip");
                if(null != eip) {
                    EipAllocateParam eipConfig  = JSONObject.parseObject(eip.toJSONString(), EipAllocateParam.class);
                    ReturnMsg checkRet = preCheckParam(eipConfig);
                    if(checkRet.getCode().equals(ReturnStatus.SC_OK)) {
                        EipOrder order = getOrderByEipParam(eipConfig.getBandwidth(), eipConfig.getIptype(),
                                eipConfig.getRegion(), eipConfig.getDuration(), eipConfig.getBillType(), "");

                        order.setConsoleCustomization(eipAllocateParam);

                        ReturnResult result = webControllerService.postOrder(order);
                        return result.getMessage();
                    }else{
                        String code = ReturnStatus.SC_PARAM_ERROR;
                        String msg = checkRet.getMessage();
                        log.error(msg);
                        JSONObject result=new JSONObject();
                        result.put("code",code);
                        result.put("msg",msg);
                        return result.toJSONString();
                    }
                }
            }
        }catch (Exception e){
            log.info("createOrder exception", e);
            JSONObject result = new JSONObject();
            result.put("code","106.999500");
            result.put("msg",e.getMessage());
            return result.toJSONString();
        }
        JSONObject result=new JSONObject();
        result.put("code",ReturnStatus.SC_INTERNAL_SERVER_ERROR);
        result.put("msg", "quota limited, user can not create eip.");
        return  result.toJSONString();
    }

    /**
     * delete eip
     * @param eipId eipid
     * @return return
     */
    public String deleteEipOrder(String eipId) {

        try{
            JSONObject eipEntity = eipAtomService.getEipEntityById(eipId);
            JSONObject eip = eipEntity.getJSONObject("eip");
            if(null == eip){
                return eipEntity.toJSONString();
            }
//            String region = eip.getString("region");
            String region = "cn-north-3";
            Integer bandwidth = eip.getInteger(HsConstants.BANDWIDTH);
            String duration = eip.getString(HsConstants.DURATION);
            String ipType = eip.getString(HsConstants.IPTYPE);
            String billType = eip.getString(HsConstants.BILLTYPE);

            EipOrder order = getOrderByEipParam(bandwidth, ipType, region, duration,billType, eipId);
            order.setOrderType(HsConstants.UNSUBSCRIBE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eipId", eipId);
            order.setConsoleCustomization(jsonObject);

            ReturnResult result= webControllerService.postOrder(order);
            log.info("OpenApi delete eip result:{}",result);
            return  result.getMessage();
        }catch (Exception e){
            log.error("Exception when deleteEipOrder", e);
            JSONObject result=new JSONObject();
            result.put("code","106.999500");
            result.put("msg",e.getMessage());
            return result.toJSONString();
        }
    }


    /**
     * get EipOrder by param
     * @param bandWidth bandwidth
     * @param ipType iptype
     * @param region region
     * @param duration duration
     * @param billType bill type
     * @param eipId id
     * @return  EipOrder
     */
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
        if(null != bearerToken && bearerToken.startsWith("Bearer ")){
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

    public String setLogLevel(String requestBody, String  packageName){
        log.info("Set debug level to:{}", requestBody);

        JSONObject jsonObject = JSON.parseObject(requestBody);
        String debugLevel = jsonObject.getString("level");
        if(null == debugLevel){
            return "failed";
        }
        try{
            Level level = Level.toLevel(debugLevel);
            Logger logger = LogManager.getLogger(packageName);
            logger.setLevel(level);
            eipAtomService.setLogLevel(requestBody, packageName);
        }catch (Exception e){
            log.error("Set log level error", e);
        }
        return "Set log level seccessfully.";
    }
}
