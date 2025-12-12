package com.flightmanagement.service;

import com.flightmanagement.dto.MonthlyStatisticsDto;
import com.flightmanagement.dto.YearlyStatisticsDto;
import com.flightmanagement.repository.StatisticsRepository;
import com.flightmanagement.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    // ==================== getYearlyStatistics Tests ====================

    @Nested
    @DisplayName("GetYearlyStatistics Tests - Full Path Coverage")
    @Tag("getYearlyStatistics")
    class GetYearlyStatisticsTests {

        private List<Object[]> mockYearlyData;

        @BeforeEach
        void setUp() {
            // Setup mock data for yearly statistics
            Object[] year2023 = new Object[]{2023, 150000L, 1200L, new BigDecimal("50000000.00")};
            Object[] year2024 = new Object[]{2024, 180000L, 1500L, new BigDecimal("65000000.00")};
            Object[] year2025 = new Object[]{2025, 200000L, 1800L, new BigDecimal("75000000.00")};
            mockYearlyData = Arrays.asList(year2023, year2024, year2025);
        }

        @Test
        @Tag("getYearlyStatistics")
        @DisplayName("Get yearly statistics - Returns list of yearly stats")
        void getYearlyStatistics_WithData_ReturnsYearlyStatsList() {
            // Arrange
            when(statisticsRepository.getStatisticsForLast5Years()).thenReturn(mockYearlyData);

            // Act
            List<YearlyStatisticsDto> result = statisticsService.getYearlyStatistics();

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            
            // Verify first entry
            YearlyStatisticsDto year2023Stats = result.get(0);
            assertEquals(2023, year2023Stats.getYear());
            assertEquals(150000L, year2023Stats.getTotalPassengers());
            assertEquals(1200L, year2023Stats.getTotalFlights());
            assertEquals(new BigDecimal("50000000.00"), year2023Stats.getTotalRevenue());
            
            // Verify last entry
            YearlyStatisticsDto year2025Stats = result.get(2);
            assertEquals(2025, year2025Stats.getYear());
            assertEquals(200000L, year2025Stats.getTotalPassengers());
            assertEquals(1800L, year2025Stats.getTotalFlights());
            assertEquals(new BigDecimal("75000000.00"), year2025Stats.getTotalRevenue());
            
            verify(statisticsRepository).getStatisticsForLast5Years();
        }

        @Test
        @Tag("getYearlyStatistics")
        @DisplayName("Get yearly statistics with empty data - Returns empty list")
        void getYearlyStatistics_WithEmptyData_ReturnsEmptyList() {
            // Arrange
            when(statisticsRepository.getStatisticsForLast5Years()).thenReturn(Collections.emptyList());

            // Act
            List<YearlyStatisticsDto> result = statisticsService.getYearlyStatistics();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(statisticsRepository).getStatisticsForLast5Years();
        }

        @Test
        @Tag("getYearlyStatistics")
        @DisplayName("Get yearly statistics with single year - Returns single entry")
        void getYearlyStatistics_WithSingleYear_ReturnsSingleEntry() {
            // Arrange
            Object[] singleYear = new Object[]{2025, 100000L, 800L, new BigDecimal("30000000.00")};
            List<Object[]> singleYearList = Collections.singletonList(singleYear);
            when(statisticsRepository.getStatisticsForLast5Years()).thenReturn(singleYearList);

            // Act
            List<YearlyStatisticsDto> result = statisticsService.getYearlyStatistics();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(2025, result.get(0).getYear());
            assertEquals(100000L, result.get(0).getTotalPassengers());
            verify(statisticsRepository).getStatisticsForLast5Years();
        }

        @Test
        @Tag("getYearlyStatistics")
        @DisplayName("Repository throws exception - Propagates exception")
        void getYearlyStatistics_RepositoryThrowsException_PropagatesException() {
            // Arrange
            when(statisticsRepository.getStatisticsForLast5Years())
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                statisticsService.getYearlyStatistics();
            });

            assertEquals("Database error", exception.getMessage());
            verify(statisticsRepository).getStatisticsForLast5Years();
        }

        @Test
        @Tag("getYearlyStatistics")
        @DisplayName("Get yearly statistics with zero values - Handles zero correctly")
        void getYearlyStatistics_WithZeroValues_HandlesCorrectly() {
            // Arrange
            Object[] yearWithZeros = new Object[]{2025, 0L, 0L, BigDecimal.ZERO};
            List<Object[]> yearWithZerosList = Collections.singletonList(yearWithZeros);
            when(statisticsRepository.getStatisticsForLast5Years()).thenReturn(yearWithZerosList);

            // Act
            List<YearlyStatisticsDto> result = statisticsService.getYearlyStatistics();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(0L, result.get(0).getTotalPassengers());
            assertEquals(0L, result.get(0).getTotalFlights());
            assertEquals(BigDecimal.ZERO, result.get(0).getTotalRevenue());
        }
    }

    // ==================== getMonthlyStatistics Tests ====================

    @Nested
    @DisplayName("GetMonthlyStatistics Tests - Full Path Coverage")
    @Tag("getMonthlyStatistics")
    class GetMonthlyStatisticsTests {

        private List<Object[]> mockMonthlyData;

        @BeforeEach
        void setUp() {
            // Setup mock data for monthly statistics
            Object[] month1 = new Object[]{1, 2025, 100L, new BigDecimal("5000000.00"), 12000L};
            Object[] month2 = new Object[]{2, 2025, 120L, new BigDecimal("6000000.00"), 14000L};
            Object[] month3 = new Object[]{3, 2025, 150L, new BigDecimal("7500000.00"), 18000L};
            mockMonthlyData = Arrays.asList(month1, month2, month3);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics for year - Returns monthly stats list")
        void getMonthlyStatistics_WithValidYear_ReturnsMonthlyStatsList() {
            // Arrange
            Integer year = 2025;
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(mockMonthlyData);

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            
            // Verify first month
            MonthlyStatisticsDto month1Stats = result.get(0);
            assertEquals(1, month1Stats.getMonth());
            assertEquals(2025, month1Stats.getYear());
            assertEquals(100L, month1Stats.getTotalFlights());
            assertEquals(new BigDecimal("5000000.00"), month1Stats.getTotalRevenue());
            assertEquals(12000L, month1Stats.getTotalPassengers());
            
            // Verify last month
            MonthlyStatisticsDto month3Stats = result.get(2);
            assertEquals(3, month3Stats.getMonth());
            assertEquals(2025, month3Stats.getYear());
            assertEquals(150L, month3Stats.getTotalFlights());
            assertEquals(new BigDecimal("7500000.00"), month3Stats.getTotalRevenue());
            assertEquals(18000L, month3Stats.getTotalPassengers());
            
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics with empty data - Returns empty list")
        void getMonthlyStatistics_WithEmptyData_ReturnsEmptyList() {
            // Arrange
            Integer year = 2025;
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(Collections.emptyList());

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics for single month - Returns single entry")
        void getMonthlyStatistics_WithSingleMonth_ReturnsSingleEntry() {
            // Arrange
            Integer year = 2025;
            Object[] singleMonth = new Object[]{12, 2025, 200L, new BigDecimal("10000000.00"), 25000L};
            List<Object[]> singleMonthList = Collections.singletonList(singleMonth);
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(singleMonthList);

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(12, result.get(0).getMonth());
            assertEquals(2025, result.get(0).getYear());
            assertEquals(200L, result.get(0).getTotalFlights());
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Repository throws exception - Propagates exception")
        void getMonthlyStatistics_RepositoryThrowsException_PropagatesException() {
            // Arrange
            Integer year = 2025;
            when(statisticsRepository.getMonthlyStatisticsByYear(year))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                statisticsService.getMonthlyStatistics(year);
            });

            assertEquals("Database error", exception.getMessage());
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics with all 12 months - Returns full year data")
        void getMonthlyStatistics_WithFullYear_ReturnsAllMonths() {
            // Arrange
            Integer year = 2025;
            List<Object[]> fullYearData = new ArrayList<>();
            for (int month = 1; month <= 12; month++) {
                fullYearData.add(new Object[]{
                    month, 
                    2025, 
                    100L + month * 10L, 
                    new BigDecimal(String.valueOf(5000000 + month * 100000)), 
                    10000L + month * 1000L
                });
            }
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(fullYearData);

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertEquals(12, result.size());
            assertEquals(1, result.get(0).getMonth());
            assertEquals(12, result.get(11).getMonth());
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics with zero values - Handles zero correctly")
        void getMonthlyStatistics_WithZeroValues_HandlesCorrectly() {
            // Arrange
            Integer year = 2025;
            Object[] monthWithZeros = new Object[]{1, 2025, 0L, BigDecimal.ZERO, 0L};
            List<Object[]> monthWithZerosList = Collections.singletonList(monthWithZeros);
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(monthWithZerosList);

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(0L, result.get(0).getTotalFlights());
            assertEquals(BigDecimal.ZERO, result.get(0).getTotalRevenue());
            assertEquals(0L, result.get(0).getTotalPassengers());
        }

        @Test
        @Tag("getMonthlyStatistics")
        @DisplayName("Get monthly statistics for past year - Returns historical data")
        void getMonthlyStatistics_ForPastYear_ReturnsHistoricalData() {
            // Arrange
            Integer year = 2020;
            Object[] pastMonth = new Object[]{6, 2020, 80L, new BigDecimal("3000000.00"), 8000L};
            List<Object[]> pastMonthList = Collections.singletonList(pastMonth);
            when(statisticsRepository.getMonthlyStatisticsByYear(year)).thenReturn(pastMonthList);

            // Act
            List<MonthlyStatisticsDto> result = statisticsService.getMonthlyStatistics(year);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(6, result.get(0).getMonth());
            assertEquals(2020, result.get(0).getYear());
            verify(statisticsRepository).getMonthlyStatisticsByYear(year);
        }
    }
}
