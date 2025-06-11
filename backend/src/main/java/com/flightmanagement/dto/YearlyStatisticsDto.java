package com.flightmanagement.dto;

import java.math.BigDecimal;

public class YearlyStatisticsDto {
    private Integer year;
    private Long totalPassengers;
    private Long totalFlights;
    private BigDecimal totalRevenue;

    public YearlyStatisticsDto() {}

    public YearlyStatisticsDto(Integer year, Long totalPassengers, Long totalFlights, BigDecimal totalRevenue) {
        this.year = year;
        this.totalPassengers = totalPassengers;
        this.totalFlights = totalFlights;
        this.totalRevenue = totalRevenue;
    }

    // Getters and setters
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }

    public Long getTotalFlights() { return totalFlights; }
    public void setTotalFlights(Long totalFlights) { this.totalFlights = totalFlights; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
