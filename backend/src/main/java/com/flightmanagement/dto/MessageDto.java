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
public class MessageDto {
    
    private Integer messageId;
    private Integer chatboxId;
    private Integer employeeId;
    private String content;
    private LocalDateTime sendTime;
    private String senderName;
    private Boolean isFromCustomer;
    private LocalDateTime deletedAt;
}
