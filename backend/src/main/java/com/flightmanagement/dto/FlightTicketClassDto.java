package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightTicketClassDto {
    
    private Integer flightId;
    private Integer ticketClassId;
    private String ticketClassName;
    private String color;
    private Integer ticketQuantity;
    private Integer remainingTicketQuantity;
    private BigDecimal specifiedFare;
    private Boolean isAvailable;
}
