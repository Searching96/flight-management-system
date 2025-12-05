package com.flightmanagement.config;

import com.mservice.config.Environment;
import com.mservice.shared.utils.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility configuration for MoMo payment gateway.
 * Delegates environment configuration to mservice Environment class which loads from environment.properties.
 * Only provides helper methods for ID generation and signature creation.
 */
@Component
@Configuration
public class MoMoConfig {

    @Value("${momo.return-url}")
    private String returnUrl;

    @Value("${momo.notify-url}")
    private String notifyUrl;

    @Value("${momo.environment:dev}")
    private String environment;

    private static MoMoConfig instance;
    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static volatile String lastDate = "";

    @jakarta.annotation.PostConstruct
    public void init() {
        instance = this;
        System.out.println("✓ MoMo configuration initialized for environment: " + environment);
        System.out.println("  - Return URL: " + returnUrl);
        System.out.println("  - Notify URL: " + notifyUrl);
    }

    /**
     * Get return URL for MoMo payment redirect
     */
    public static String getReturnUrl() {
        if (instance == null) {
            throw new IllegalStateException("MoMo configuration not initialized");
        }
        return instance.returnUrl;
    }

    /**
     * Get notify URL for MoMo IPN callback
     */
    public static String getNotifyUrl() {
        if (instance == null) {
            throw new IllegalStateException("MoMo configuration not initialized");
        }
        return instance.notifyUrl;
    }

    /**
     * Get MoMo Environment instance from mservice configuration
     * Loads from environment.properties as configured in mservice
     */
    public static Environment getMoMoEnvironment() {
        if (instance == null) {
            throw new IllegalStateException("MoMo configuration not initialized");
        }
        return Environment.selectEnv(instance.environment);
    }

    /**
     * Get access key from Environment's PartnerInfo
     */
    public static String getAccessKey() {
        return getMoMoEnvironment().getPartnerInfo().getAccessKey();
    }

    /**
     * Generate HMAC_SHA256 signature using mservice Encoder utility and Environment's secret key
     */
    public static String generateHmacSHA256Signature(final String data) {
        if (data == null) {
            throw new IllegalStateException("Data is null");
        }
        try {
            Environment env = getMoMoEnvironment();
            String secretKey = env.getPartnerInfo().getSecretKey();
            return Encoder.signHmacSHA256(data, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Error creating HMAC SHA256 signature", e);
        }
    }

    /**
     * Generate unique request ID with date prefix (YYYYMMDDNNNNNN)
     */
    public static String getUniqueRequestId() {
        String today = LocalDate.now().toString().replace("-", "");
        if (!today.equals(lastDate)) {
            sequence.set(0);
            lastDate = today;
        }
        int seq = sequence.incrementAndGet();
        return today + String.format("%06d", seq);
    }

    /**
     * Generate unique order ID (timestamp-based)
     */
    public static String getUniqueOrderId() {
        return String.valueOf(System.currentTimeMillis());
    }
}