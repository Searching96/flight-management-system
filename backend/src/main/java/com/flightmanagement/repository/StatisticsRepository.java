package com.flightmanagement.repository;

import com.flightmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Employee, Integer> {

    @Query(value = "SELECT y.year, " +
            "COALESCE(COUNT(DISTINCT t.passenger_id), 0) as totalPassengers, " +
            "COALESCE(COUNT(DISTINCT f.flight_id), 0) as totalFlights, " +
            "COALESCE(SUM(t.fare), 0) as totalRevenue " +
            "FROM (SELECT YEAR(CURDATE()) - 4 as year UNION " +
            "      SELECT YEAR(CURDATE()) - 3 UNION " +
            "      SELECT YEAR(CURDATE()) - 2 UNION " +
            "      SELECT YEAR(CURDATE()) - 1 UNION " +
            "      SELECT YEAR(CURDATE())) y " +
            "LEFT JOIN flight f ON YEAR(f.departure_time) = y.year AND f.deleted_at IS NULL " +
            "LEFT JOIN ticket t ON f.flight_id = t.flight_id AND t.ticket_status = 1 AND t.deleted_at IS NULL " +
            "GROUP BY y.year " +
            "ORDER BY y.year", nativeQuery = true)
    List<Object[]> getStatisticsForLast5Years();

    @Query(value = "SELECT m.month, " +
            ":year as year, " +
            "COALESCE(COUNT(DISTINCT f.flight_id), 0) as totalFlights, " +
            "COALESCE(SUM(t.fare), 0) as totalRevenue, " +
            "COALESCE(COUNT(DISTINCT t.passenger_id), 0) as totalPassengers " +
            "FROM (SELECT 1 as month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION " +
            "      SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION " +
            "      SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
            "LEFT JOIN flight f ON MONTH(f.departure_time) = m.month AND YEAR(f.departure_time) = :year AND f.deleted_at IS NULL " +
            "LEFT JOIN ticket t ON f.flight_id = t.flight_id AND t.ticket_status = 1 AND t.deleted_at IS NULL " +
            "GROUP BY m.month " +
            "ORDER BY m.month", nativeQuery = true)
    List<Object[]> getMonthlyStatisticsByYear(@Param("year") Integer year);
}
