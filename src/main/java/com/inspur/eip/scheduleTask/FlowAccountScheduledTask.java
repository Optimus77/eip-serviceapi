package com.inspur.eip.scheduleTask;

import com.inspur.eip.entity.bss.FlowAccount2Bss;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.FlowService;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description 流量计费，日志类报文定时任务
 * @Author Zerah
 * @Date 2019/7/24 10:19
 **/
@Component
@Slf4j
@EnableScheduling
public class FlowAccountScheduledTask {

    @Autowired
    private FlowService flowService;

    @Autowired
    private EipDaoService eipDaoService;


//    每分钟统计
//    @Scheduled(cron = "0 0/1 * * * * ")
    //    每小时统计
    @Scheduled(cron = "0 0 0/1 * * *")
    public void oneHourReportFlowAccount(){
        try {
            List<Eip> trafficEips = eipDaoService.findFlowAccountEipList("Traffic");
            log.info("start to report flow eip to bss:{}",trafficEips);
            if (trafficEips!=null && trafficEips.size()>0){
                for (Eip eip : trafficEips) {
                    Map<String, Long> map = flowService.staticsFlowByPeriod(60, eip.getEipAddress(),  "lasthour", eip.getFirewallId());
                    if (map.containsKey(HillStoneConfigConsts.UP_TYPE)){
                        Long up = map.get(HillStoneConfigConsts.UP_TYPE);
                        Long down = map.get(HillStoneConfigConsts.DOWN_TYPE);
                        Long sum = map.get(HillStoneConfigConsts.SUM_TYPE);
                        FlowAccount2Bss flowBean = flowService.getFlowAccount2BssBean(eip, up, down, sum);
                        //给 Bss发送报文
                        flowService.sendOrderMessageToBss(flowBean);
                    }
                }
            }
            return;
        } catch (Exception e) {
            log.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()+":{}",e.getMessage());
        }
    }
}
