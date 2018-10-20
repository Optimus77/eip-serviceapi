package com.inspur.eip;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class EipServiceApplication  {

    public static void main(String[] args) {
        SpringApplication.run(EipServiceApplication.class, args);
    }


}
