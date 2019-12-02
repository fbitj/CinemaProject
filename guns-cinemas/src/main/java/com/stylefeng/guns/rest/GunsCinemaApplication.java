package com.stylefeng.guns.rest;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.stylefeng.guns"})
@EnableDubboConfiguration//容易忘，牢记
public class GunsCinemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GunsCinemaApplication.class, args);
    }
}
