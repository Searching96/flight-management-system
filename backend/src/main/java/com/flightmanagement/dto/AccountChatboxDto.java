package com.flightmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountChatboxDto {
    private Integer accountId;
    private Integer chatboxId;
    private String accountName;
    private LocalDateTime lastVisitTime;
    private Long unreadMessageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
