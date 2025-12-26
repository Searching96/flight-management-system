package com.flightmanagement.repository;

import com.flightmanagement.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    
    List<AuditLog> findByEntityName(String entityName);
    
    List<AuditLog> findByEntityNameAndEntityId(String entityName, String entityId);
    
    Page<AuditLog> findByEntityName(String entityName, Pageable pageable);
    
    Page<AuditLog> findByEntityNameAndEntityId(String entityName, String entityId, Pageable pageable);
    
    List<AuditLog> findByChangedBy(String changedBy);
    
    @Query("SELECT a FROM AuditLog a WHERE a.changedAt BETWEEN :startDate AND :endDate ORDER BY a.changedAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    List<AuditLog> findAllByOrderByChangedAtDesc();
    
    Page<AuditLog> findAllByOrderByChangedAtDesc(Pageable pageable);
}
