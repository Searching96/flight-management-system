package com.flightmanagement.repository;

import com.flightmanagement.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE m.chatboxId = ?1 AND m.deletedAt IS NULL ORDER BY m.sendTime ASC")
    List<Message> findByChatboxIdOrderBySendTimeAsc(Integer chatboxId);
    
    @Query("SELECT m FROM Message m WHERE m.chatboxId = ?1 AND m.deletedAt IS NULL ORDER BY m.sendTime DESC LIMIT 1")
    Optional<Message> findTopByChatboxIdOrderBySendTimeDesc(Integer chatboxId);
    
    @Query("SELECT m FROM Message m WHERE m.chatboxId = ?1 AND m.employeeId IS NULL AND m.deletedAt IS NULL ORDER BY m.sendTime DESC LIMIT 1")
    Optional<Message> findLatestCustomerMessageByChatboxId(Integer chatboxId);
    
    @Query("SELECT m.chatboxId, MAX(m.sendTime) FROM Message m WHERE m.employeeId IS NULL AND m.deletedAt IS NULL GROUP BY m.chatboxId")
    List<Object[]> findLatestCustomerMessageTimesByChatbox();
    
    @Query("SELECT DISTINCT m.employeeId FROM Message m WHERE m.chatbox.chatboxId = :chatboxId AND m.employeeId IS NOT NULL")
    List<Integer> findDistinctEmployeeIdsByChatboxId(@Param("chatboxId") Integer chatboxId);
}
