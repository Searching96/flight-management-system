package com.flightmanagement.controller;

import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.entity.AuditLog;
import com.flightmanagement.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Log", description = "Audit logging operations")
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }
    
    @Operation(summary = "Get all audit logs with pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogs = auditLogService.getAllAuditLogs(pageable);
        
        ApiResponse<Page<AuditLog>> response = new ApiResponse<>(
            HttpStatus.OK,
            "Audit logs retrieved successfully",
            auditLogs,
            null
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get audit logs for a specific entity")
    @GetMapping("/{entityName}/{entityId}")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogsByEntity(
            @PathVariable String entityName,
            @PathVariable String entityId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEntity(entityName, entityId);
        
        ApiResponse<List<AuditLog>> response = new ApiResponse<>(
            HttpStatus.OK,
            "Audit logs retrieved successfully",
            auditLogs,
            null
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get audit logs by date range")
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByDateRange(startDate, endDate);
        
        ApiResponse<List<AuditLog>> response = new ApiResponse<>(
            HttpStatus.OK,
            "Audit logs retrieved successfully",
            auditLogs,
            null
        );
        return ResponseEntity.ok(response);
    }
}
