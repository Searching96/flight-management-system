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
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.deletedAt IS NULL")
    List<AccountChatbox> findAllActive();
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.accountId = :accountId AND ac.chatboxId = :chatboxId AND ac.deletedAt IS NULL")
    Optional<AccountChatbox> findByAccountIdAndChatboxId(@Param("accountId") Integer accountId, @Param("chatboxId") Integer chatboxId);
    
    @Query("SELECT ac FROM AccountChatbox ac WHERE ac.accountId = :accountId AND ac.deletedAt IS NULL")
    List<AccountChatbox> findByAccountId(@Param("accountId") Integer accountId);
    
    @Query("SELECT COALESCE(COUNT(m), 0) FROM Message m " +
           "WHERE m.chatboxId = :chatboxId " +
           "AND m.sendTime > (SELECT ac.lastVisitTime FROM AccountChatbox ac WHERE ac.accountId = :accountId AND ac.chatboxId = :chatboxId) " +
           "AND m.deletedAt IS NULL")
    Long countUnreadMessages(@Param("accountId") Integer accountId, @Param("chatboxId") Integer chatboxId);
}
