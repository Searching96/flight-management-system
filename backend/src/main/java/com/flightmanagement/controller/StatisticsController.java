package com.flightmanagement.controller;

import com.flightmanagement.dto.MonthlyStatisticsDto;
import com.flightmanagement.dto.YearlyStatisticsDto;
import com.flightmanagement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/yearly")
    public ResponseEntity<List<YearlyStatisticsDto>> getYearlyStatistics() {
        try {
            List<YearlyStatisticsDto> statistics = statisticsService.getYearlyStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("Error in getYearlyStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/monthly/{year}")
    public ResponseEntity<List<MonthlyStatisticsDto>> getMonthlyStatistics(@PathVariable Integer year) {
        try {
            List<MonthlyStatisticsDto> statistics = statisticsService.getMonthlyStatistics(year);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            System.err.println("Error in getMonthlyStatistics for year " + year + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
