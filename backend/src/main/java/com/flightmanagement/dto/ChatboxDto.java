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
    private Integer employeeId;
    private String employeeName;
    private LocalDateTime lastMessageTime;
    private String lastMessageContent;
    private Integer unreadCount;
}
