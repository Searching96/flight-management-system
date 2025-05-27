package com.flightmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlightTicketClassDto {
    private Integer flightId;
    private Integer ticketClassId;
    private String ticketClassName;
    private String color;
    private String flightCode;
    private Integer ticketQuantity;
    private Integer remainingTicketQuantity;
    private BigDecimal specifiedFare;
    private Boolean isAvailable;
}
