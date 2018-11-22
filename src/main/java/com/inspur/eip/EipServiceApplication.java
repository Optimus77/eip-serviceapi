package com.inspur.eip;

import com.inspur.eip.filter.CrosControllerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.inspur"})
@ServletComponentScan
public class EipServiceApplication  {

    public static void main(String[] args) {
        SpringApplication.run(EipServiceApplication.class, args);
//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CrosControllerFilter crosControllerFilter = new CrosControllerFilter();
        registrationBean.setFilter(crosControllerFilter);
        return registrationBean;
    }


}
