package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.util.constant.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 流量计费实现类
 * @Author Zerah
 * @Date 2019/7/19 10:34
 **/
@Slf4j
@Service
public class FlowService {

    public static final String SPACE_REGEX ="\\s+";

    @Autowired
    private FirewallService firewallService;

    public Map<String, Long> staticsFlowByPeriod(int lineNum, String entryName, String period, String firewallId) {

        ConcurrentHashMap<String, Long> flowMap = new ConcurrentHashMap<>(3);
        if (StringUtils.isBlank(entryName) || StringUtils.isBlank(period)) {
            log.warn(ErrorStatus.VALIADATE_NAME_ERROR.getMessage() + "entryName:{}，period:{}", entryName, period);
            throw new EipBadRequestException(ErrorStatus.VALIADATE_NAME_ERROR.getCode(), ErrorStatus.VALIADATE_NAME_ERROR.getMessage());
        }
// flowStr not null
        JSONObject json = firewallService.cmdShowStatisticsByAddressBook(entryName, period, firewallId);
        String[] upArrs = json.getString("UP").replace("UP  :", " ").trim().split(SPACE_REGEX);
        String[] downArrs = json.getString("DOWN").replace("DOWN:", " ").trim().split(SPACE_REGEX);

        int[] upInt = new int[upArrs.length];
        int[] downInt = new int[downArrs.length];
        if (upInt.length == downInt.length){
            for (int i= 0; i<upInt.length; i++){
                upInt[i] = Integer.parseInt(upArrs[i]);
                downInt[i] = Integer.parseInt(downArrs[i]);
            }
        }
        long upFlow = 0;
        long downFlow = 0;
//  1：每分钟统计  5：每五分钟统计  0:统计所有
        if (lineNum ==60 || lineNum == 1 || lineNum ==5){
            for (int i=0; i<lineNum*2;i++){
                upFlow = upFlow + upInt[i] ;
                downFlow = downFlow + downInt[i];
            }

            flowMap.put("UP",upFlow);
            flowMap.put("DOWN",downFlow);
            flowMap.put("SUM",upFlow + downFlow);
        }else {
            log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage()+ "lineNum:{}", lineNum);
            throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
        return flowMap;
    }

}
