package com.flightmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class FlightManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightManagementSystemApplication.class, args);
    }
}
