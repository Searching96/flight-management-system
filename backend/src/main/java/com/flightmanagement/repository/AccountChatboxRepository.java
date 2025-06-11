package com.flightmanagement.repository;

import com.flightmanagement.entity.AccountChatbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountChatboxRepository extends JpaRepository<AccountChatbox, Integer> {
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.accountId = :accountId AND ac.chatboxId = :chatboxId AND ac.deletedAt IS NULL")
    Optional<AccountChatbox> findByAccountIdAndChatboxId(@Param("accountId") Integer accountId, @Param("chatboxId") Integer chatboxId);
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.accountId = :accountId AND ac.deletedAt IS NULL")
    List<AccountChatbox> findByAccountId(@Param("accountId") Integer accountId);
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.chatboxId = :chatboxId AND ac.deletedAt IS NULL")
    List<AccountChatbox> findByChatboxId(@Param("chatboxId") Integer chatboxId);
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.deletedAt IS NULL")
    List<AccountChatbox> findAllActive();
    
    @Query("SELECT COUNT(m) FROM Message m " +
           "JOIN AccountChatbox ac ON m.chatboxId = ac.chatboxId " +
           "WHERE ac.accountId = :accountId AND ac.chatboxId = :chatboxId " +
           "AND m.sendTime > ac.lastVisitTime AND m.deletedAt IS NULL")
    Long countUnreadMessages(@Param("accountId") Integer accountId, @Param("chatboxId") Integer chatboxId);
}
