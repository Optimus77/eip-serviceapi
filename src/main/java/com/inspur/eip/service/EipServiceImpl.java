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
        }catch (Exception e){
            log.error("Set log level error", e);
        }
        return "Set log level seccessfully.";
    }
}
