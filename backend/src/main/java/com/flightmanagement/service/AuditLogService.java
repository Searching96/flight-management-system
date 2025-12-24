package com.flightmanagement.service;

import com.flightmanagement.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    
    void saveAuditLog(String entityName, String entityId, String action, 
                     String fieldName, String oldValue, String newValue, String changedBy);
    
    List<AuditLog> getAuditLogsByEntity(String entityName, String entityId);
    
    Page<AuditLog> getAllAuditLogs(Pageable pageable);
    
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
