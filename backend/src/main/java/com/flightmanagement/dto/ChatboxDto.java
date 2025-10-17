package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatboxDto {
    
    private Integer chatboxId;
    private Integer customerId;
    private String customerName;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Boolean isLastMessageFromCustomer;
    private Integer lastMessageEmployeeId;
    private String lastMessageSenderName;
    private Integer unreadCount;
    private LocalDateTime lastCustomerMessageTime; // New field for sorting
    private Integer employeeSupportCount; // Add this field
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt; // Add createdAt for fallback sorting
}
