package com.blubugtech.bakery_notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.blubakery.bakery_common_libs.security.MethodSecurityConfig;
import org.blubakery.bakery_common_libs.kafka.KafkaConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
@EnableAsync
@EnableScheduling
@Import({MethodSecurityConfig.class, KafkaConfig.class})
public class BakeryNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BakeryNotificationServiceApplication.class, args);
    }

}

