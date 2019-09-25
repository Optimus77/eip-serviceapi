package com.inspur.eip.scheduleTask;

import com.inspur.eip.entity.bss.FlowAccount2Bss;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.service.EipDaoService;
import com.inspur.eip.service.FlowService;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
@EnableAsync
public class FlowAccountScheduledTask {

    @Autowired
    private FlowService flowService;

    @Autowired
    private EipRepository eipRepository;

    //    异步统计每小时流量数据,并发送给Bss侧
    @Scheduled(cron = "0 0/5 * * * *")
    @Async
    public void oneHourReportFlowAccount(){
        try {
            List<Eip> flowEipList = eipRepository.findByBillTypeAndIsDelete(HsConstants.HOURLYNETFLOW,0);
            log.debug("Traffic eip List:{}",flowEipList);
            if (flowEipList!=null && flowEipList.size()>0){
                for (Eip eip : flowEipList) {
                    Map<String, Long> map = flowService.staticsFlowByPeriod(60, eip.getEipAddress(),  "lasthour", eip.getFirewallId());
                    if (map.containsKey(HillStoneConfigConsts.UP_TYPE)){
                        Long up = map.get(HillStoneConfigConsts.UP_TYPE);
                        FlowAccount2Bss flowBean = flowService.getFlowAccount2BssBean(eip, up, true);
                        //给 Bss发送报文
                        flowService.sendOrderMessageToBss(flowBean);
                    }else {
                        log.warn("This eip cant statitics from firewall ,eip:{}",eip );
                        FlowAccount2Bss flowBean = flowService.getFlowAccount2BssBean(eip, 0L, true);
                        flowService.sendOrderMessageToBss(flowBean);
                    }
                }
            }
            return;
        } catch (Exception e) {
            log.error(ErrorStatus.ENTITY_INTERNAL_SERVER_ERROR.getMessage()+":{}",e);
        }
    }
}
