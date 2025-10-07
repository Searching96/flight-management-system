package com.flightmanagement.service.impl;

import com.flightmanagement.dto.MonthlyStatisticsDto;
import com.flightmanagement.dto.YearlyStatisticsDto;
import com.flightmanagement.repository.StatisticsRepository;
import com.flightmanagement.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

   private final StatisticsRepository statisticsRepository;

   public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
   }

   public List<YearlyStatisticsDto> getYearlyStatistics() {
      List<Object[]> results = statisticsRepository.getStatisticsForLast5Years();
      List<YearlyStatisticsDto> yearlyStats = new ArrayList<>();

      for (Object[] result : results) {
         YearlyStatisticsDto dto = new YearlyStatisticsDto(
               ((Number) result[0]).intValue(), // year
               ((Number) result[1]).longValue(), // totalPassengers
               ((Number) result[2]).longValue(), // totalFlights
               (BigDecimal) result[3] // totalRevenue
         );
         yearlyStats.add(dto);
      }

      return yearlyStats;
   }

   public List<MonthlyStatisticsDto> getMonthlyStatistics(Integer year) {
      List<Object[]> results = statisticsRepository.getMonthlyStatisticsByYear(year);
      List<MonthlyStatisticsDto> monthlyStats = new ArrayList<>();

      for (Object[] result : results) {
         MonthlyStatisticsDto dto = new MonthlyStatisticsDto(
               ((Number) result[0]).intValue(), // month
               ((Number) result[1]).intValue(), // year
               ((Number) result[2]).longValue(), // totalFlights
               (BigDecimal) result[3], // totalRevenue
               ((Number) result[4]).longValue() // totalPassengers
         );
         monthlyStats.add(dto);
      }

      return monthlyStats;
   }
}
