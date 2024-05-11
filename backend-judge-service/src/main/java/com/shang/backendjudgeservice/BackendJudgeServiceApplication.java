package com.shang.backendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.shang")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.shang.backendserviceclient.service"})
public class BackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendJudgeServiceApplication.class, args);
    }

}
