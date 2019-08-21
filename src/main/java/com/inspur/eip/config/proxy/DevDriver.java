package com.inspur.eip.config.proxy;

import com.inspur.eip.service.FirewallService;
import com.inspur.eip.service.IDevProvider;
import com.inspur.eip.service.LbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Slf4j
@Configuration
public class DevDriver {


    @Bean(name="lbService")
    @ConditionalOnProperty(value = "firewall.type",havingValue = "radware")
    public LbService lbService(){
        return new LbService();
    }

//    @Bean(name="firewallService")
//    @ConditionalOnProperty(value = "firewall.type",havingValue = "hillstone")
//    public FirewallService FirewallService(){
//        return new FirewallService();
//    }

}
