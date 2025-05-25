package com.flightmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class FlightManagementSystemApplication {

    public static void main(String[] args) {
        System.out.println("🛫 Starting Flight Management System...");
        SpringApplication.run(FlightManagementSystemApplication.class, args);
        System.out.println("✅ Flight Management System started successfully!");
        System.out.println("📍 API Documentation: http://localhost:8080/api/demo/info");
        System.out.println("🏥 Health Check: http://localhost:8080/api/demo/health");
    }
}
