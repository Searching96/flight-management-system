package com.flightmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_chatbox")
@IdClass(AccountChatboxId.class)
public class AccountChatbox {
    
    @Id
    @Column(name = "account_id")
    private Integer accountId;
    
    @Id
    @Column(name = "chatbox_id")
    private Integer chatboxId;
    
    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "chatbox_id", insertable = false, updatable = false)
    private Chatbox chatbox;
    
    @Column(name = "last_visit_time")
    private LocalDateTime lastVisitTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

// Composite key class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class AccountChatboxId implements java.io.Serializable {
    private Integer accountId;
    private Integer chatboxId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountChatboxId that = (AccountChatboxId) o;
        return java.util.Objects.equals(accountId, that.accountId) && 
               java.util.Objects.equals(chatboxId, that.chatboxId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(accountId, chatboxId);
    }
}
