package com.shang.backenduserservice.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableConfigurationProperties(MinioInfo.class)
@Slf4j
public class MinioConfig {

    @Autowired
    private MinioInfo minioInfo;

    /**
     * 获取 MinioClient
     */
    @Bean
    public MinioClient minioClient() throws NoSuchAlgorithmException, KeyManagementException {
        log.info("minio配置类开始加载,{}",minioInfo.getEndpoint());
        return MinioClient.builder()
                .endpoint(minioInfo.getEndpoint())
                .credentials(minioInfo.getAccessKey(), minioInfo.getSecretKey())
                .build();
    }
}