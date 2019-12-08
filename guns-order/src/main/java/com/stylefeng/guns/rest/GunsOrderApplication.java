package com.stylefeng.guns.rest;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableDubboConfiguration
public class GunsOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GunsOrderApplication.class, args);
    }
}
