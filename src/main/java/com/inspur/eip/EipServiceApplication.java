package com.inspur.eip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com"})
public class EipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EipServiceApplication.class, args);
    }
}
