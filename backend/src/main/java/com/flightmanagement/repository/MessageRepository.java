package com.flightmanagement.repository;

import com.flightmanagement.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE m.deletedAt IS NULL")
    List<Message> findAllActive();
    
    @Query("SELECT m FROM Message m WHERE m.chatbox.chatboxId = ?1 AND m.deletedAt IS NULL ORDER BY m.sendTime ASC")
    List<Message> findByChatboxIdOrderBySendTime(Integer chatboxId);
    
    @Query("SELECT m FROM Message m WHERE m.chatbox.chatboxId = ?1 AND m.deletedAt IS NULL ORDER BY m.sendTime DESC")
    List<Message> findByChatboxIdOrderBySendTimeDesc(Integer chatboxId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.messageType = ?1 AND m.deletedAt IS NULL")
    List<Message> findByMessageType(Integer messageType);
}
