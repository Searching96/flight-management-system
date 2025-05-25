package com.flightmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@EnableTransactionManagement
@EnableCaching
public class DatabaseConfig {
    // Transaction and caching configurations
}
