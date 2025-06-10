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
public class TicketSearchCriteria {
    private String status;
    private Integer flightId;
    private Integer customerId;
    private Integer ticketClassId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
