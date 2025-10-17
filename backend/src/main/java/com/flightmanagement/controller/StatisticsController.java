package com.flightmanagement.controller;

import com.flightmanagement.dto.MonthlyStatisticsDto;
import com.flightmanagement.dto.YearlyStatisticsDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Operations related to statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Get yearly statistics")
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse<List<YearlyStatisticsDto>>> getYearlyStatistics() {
        try {
            List<YearlyStatisticsDto> statistics = statisticsService.getYearlyStatistics();
            ApiResponse<List<YearlyStatisticsDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Yearly statistics retrieved successfully",
                    statistics,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error in getYearlyStatistics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get monthly statistics for a specific year")
    @GetMapping("/monthly/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyStatisticsDto>>> getMonthlyStatistics(@PathVariable Integer year) {
        try {
            List<MonthlyStatisticsDto> statistics = statisticsService.getMonthlyStatistics(year);
            ApiResponse<List<MonthlyStatisticsDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Monthly statistics for year " + year + " retrieved successfully",
                    statistics,
                    null
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error in getMonthlyStatistics for year " + year + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
