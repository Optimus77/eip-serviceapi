package com.inspur.eip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.inspur"})
@ServletComponentScan
@EnableTransactionManagement
public class EipServiceApplicationTests {

    public static void main(String[] args) { SpringApplication.run(EipServiceApplicationTests.class, args);}

}
