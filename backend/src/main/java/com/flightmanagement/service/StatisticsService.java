package com.flightmanagement.service;

import com.flightmanagement.dto.MonthlyStatisticsDto;
import com.flightmanagement.dto.YearlyStatisticsDto;

import java.util.List;

public interface StatisticsService {

    List<YearlyStatisticsDto> getYearlyStatistics();
    
    List<MonthlyStatisticsDto> getMonthlyStatistics(Integer year);
}
