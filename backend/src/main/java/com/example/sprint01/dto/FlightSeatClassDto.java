package com.example.sprint01.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSeatClassDto {
    private Long flightId;
    private Long seatClassId;
    private int totalTickets;
    private int remainingTickets;
    private int currentPrice;
}
