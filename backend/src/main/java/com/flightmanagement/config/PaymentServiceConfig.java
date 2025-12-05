package com.flightmanagement.config;

import com.flightmanagement.service.PaymentService;
import com.flightmanagement.service.impl.MoMoServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for MoMo payment service
 */
@Configuration
public class PaymentServiceConfig {

    /**
     * Primary payment service bean - MoMo payment gateway
     */
    @Bean
    @Primary
    public PaymentService momoPaymentService(MoMoServiceImpl momoService) {
        System.out.println("✓ MoMo Payment Gateway activated as primary payment service");
        return momoService;
    }
}