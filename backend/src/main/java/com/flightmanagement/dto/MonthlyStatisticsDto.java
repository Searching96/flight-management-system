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
public class MonthlyStatisticsDto {

    private Integer month;
    private Integer year;
    private Long totalFlights;
    private BigDecimal totalRevenue;
    private Long totalPassengers;
}
