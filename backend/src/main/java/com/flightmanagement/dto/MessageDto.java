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
    private Integer employeeId;  // null = from customer, not null = from employee
    private String employeeName;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isFromCustomer;  // Derived field: true if employeeId is null
    private LocalDateTime deletedAt;
}
