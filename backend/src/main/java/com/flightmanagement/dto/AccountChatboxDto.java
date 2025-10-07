package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountChatboxDto {

    private Integer accountId;
    private Integer chatboxId;
    private String accountName;
    private LocalDateTime lastVisitTime;
    private Long unreadMessageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
