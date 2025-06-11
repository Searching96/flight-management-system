package com.flightmanagement.dto;

import java.math.BigDecimal;

public class MonthlyStatisticsDto {
    private Integer month;
    private Integer year;
    private Long totalFlights;
    private BigDecimal totalRevenue;
    private Long totalPassengers;

    public MonthlyStatisticsDto() {}

    public MonthlyStatisticsDto(Integer month, Integer year, Long totalFlights, BigDecimal totalRevenue, Long totalPassengers) {
        this.month = month;
        this.year = year;
        this.totalFlights = totalFlights;
        this.totalRevenue = totalRevenue;
        this.totalPassengers = totalPassengers;
    }

    // Getters and setters
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Long getTotalFlights() { return totalFlights; }
    public void setTotalFlights(Long totalFlights) { this.totalFlights = totalFlights; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }
}
