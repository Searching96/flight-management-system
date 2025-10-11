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
public class YearlyStatisticsDto {

    private Integer year;
    private Long totalPassengers;
    private Long totalFlights;
    private BigDecimal totalRevenue;
}
