package com.flightmanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("message", "Flight Management System is running");
        status.put("version", "1.0.0-DEMO");
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Flight Management System");
        info.put("description", "Comprehensive flight booking and management system");
        info.put("version", "1.0.0-DEMO");
        info.put("features", new String[]{
            "Flight Search & Booking",
            "Airport Management", 
            "Plane Fleet Management",
            "Customer Management",
            "Real-time Chat Support",
            "System Parameters Configuration"
        });
        info.put("demo_accounts", Map.of(
            "admin", "admin@flightmanagement.com / admin123",
            "customer", "customer@demo.com / demo123"
        ));
        return ResponseEntity.ok(info);
    }
}
