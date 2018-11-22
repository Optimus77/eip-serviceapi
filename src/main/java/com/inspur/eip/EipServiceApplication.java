package com.inspur.eip;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication(scanBasePackages = {"com.inspur"})
@ServletComponentScan
public class EipServiceApplication  {

    public static void main(String[] args) {
        SpringApplication.run(EipServiceApplication.class, args);
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }


}
