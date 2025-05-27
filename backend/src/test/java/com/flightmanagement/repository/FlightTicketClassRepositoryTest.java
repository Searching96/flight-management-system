package com.flightmanagement.repository;

import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.TicketClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FlightTicketClassRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightTicketClassRepository flightTicketClassRepository;

    private Flight testFlight;
    private TicketClass testTicketClass;
    private FlightTicketClass testFlightTicketClass;

    @BeforeEach
    void setUp() {
        // Create test flight
        testFlight = new Flight();
        testFlight.setFlightCode("AA123");
        testFlight.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        testFlight = entityManager.persistAndFlush(testFlight);

        // Create test ticket class
        testTicketClass = new TicketClass();
        testTicketClass.setTicketClassName("Economy");
        testTicketClass.setColor("blue");
        testTicketClass = entityManager.persistAndFlush(testTicketClass);

        // Create test flight ticket class
        testFlightTicketClass = new FlightTicketClass();
        testFlightTicketClass.setFlightId(testFlight.getFlightId());
        testFlightTicketClass.setTicketClassId(testTicketClass.getTicketClassId());
        testFlightTicketClass.setFlight(testFlight);
        testFlightTicketClass.setTicketClass(testTicketClass);
        testFlightTicketClass.setTicketQuantity(100);
        testFlightTicketClass.setRemainingTicketQuantity(80);
        testFlightTicketClass.setSpecifiedFare(new BigDecimal("299.99"));
        testFlightTicketClass = entityManager.persistAndFlush(testFlightTicketClass);

        entityManager.clear();
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveRecords() {
        // Arrange
        FlightTicketClass deletedFlightTicketClass = new FlightTicketClass();
        deletedFlightTicketClass.setFlightId(testFlight.getFlightId());
        deletedFlightTicketClass.setTicketClassId(testTicketClass.getTicketClassId());
        deletedFlightTicketClass.setFlight(testFlight);
        deletedFlightTicketClass.setTicketClass(testTicketClass);
        deletedFlightTicketClass.setTicketQuantity(50);
        deletedFlightTicketClass.setRemainingTicketQuantity(0);
        deletedFlightTicketClass.setSpecifiedFare(new BigDecimal("199.99"));
        deletedFlightTicketClass.setDeletedAt(LocalDateTime.now());
        entityManager.persistAndFlush(deletedFlightTicketClass);

        // Act
        List<FlightTicketClass> result = flightTicketClassRepository.findAllActive();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testFlightTicketClass.getFlightId(), result.get(0).getFlightId());
        assertEquals(testFlightTicketClass.getTicketClassId(), result.get(0).getTicketClassId());
    }

    @Test
    void findByFlightIdAndTicketClassId_WhenExists_ShouldReturnFlightTicketClass() {
        // Act
        Optional<FlightTicketClass> result = flightTicketClassRepository.findByFlightIdAndTicketClassId(
                testFlight.getFlightId(), testTicketClass.getTicketClassId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testFlightTicketClass.getFlightId(), result.get().getFlightId());
        assertEquals(testFlightTicketClass.getTicketClassId(), result.get().getTicketClassId());
        assertEquals(testFlightTicketClass.getSpecifiedFare(), result.get().getSpecifiedFare());
    }

    @Test
    void findByFlightIdAndTicketClassId_WhenNotExists_ShouldReturnEmpty() {
        // Act
        Optional<FlightTicketClass> result = flightTicketClassRepository.findByFlightIdAndTicketClassId(999, 999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByFlightIdAndTicketClassId_WhenDeleted_ShouldReturnEmpty() {
        // Arrange
        testFlightTicketClass.setDeletedAt(LocalDateTime.now());
        entityManager.merge(testFlightTicketClass);
        entityManager.flush();

        // Act
        Optional<FlightTicketClass> result = flightTicketClassRepository.findByFlightIdAndTicketClassId(
                testFlight.getFlightId(), testTicketClass.getTicketClassId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByFlightId_ShouldReturnAllClassesForFlight() {
        // Arrange
        TicketClass businessClass = new TicketClass();
        businessClass.setTicketClassName("Business");
        businessClass.setColor("gold");
        businessClass = entityManager.persistAndFlush(businessClass);

        FlightTicketClass businessFlightTicketClass = new FlightTicketClass();
        businessFlightTicketClass.setFlightId(testFlight.getFlightId());
        businessFlightTicketClass.setTicketClassId(businessClass.getTicketClassId());
        businessFlightTicketClass.setFlight(testFlight);
        businessFlightTicketClass.setTicketClass(businessClass);
        businessFlightTicketClass.setTicketQuantity(20);
        businessFlightTicketClass.setRemainingTicketQuantity(15);
        businessFlightTicketClass.setSpecifiedFare(new BigDecimal("599.99"));
        entityManager.persistAndFlush(businessFlightTicketClass);

        // Act
        List<FlightTicketClass> result = flightTicketClassRepository.findByFlightId(testFlight.getFlightId());

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void findByTicketClassId_ShouldReturnAllFlightsForClass() {
        // Arrange
        Flight secondFlight = new Flight();
        secondFlight.setFlightCode("BB456");
        secondFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        secondFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(4));
        secondFlight = entityManager.persistAndFlush(secondFlight);

        FlightTicketClass secondFlightTicketClass = new FlightTicketClass();
        secondFlightTicketClass.setFlightId(secondFlight.getFlightId());
        secondFlightTicketClass.setTicketClassId(testTicketClass.getTicketClassId());
        secondFlightTicketClass.setFlight(secondFlight);
        secondFlightTicketClass.setTicketClass(testTicketClass);
        secondFlightTicketClass.setTicketQuantity(120);
        secondFlightTicketClass.setRemainingTicketQuantity(100);
        secondFlightTicketClass.setSpecifiedFare(new BigDecimal("349.99"));
        entityManager.persistAndFlush(secondFlightTicketClass);

        // Act
        List<FlightTicketClass> result = flightTicketClassRepository.findByTicketClassId(testTicketClass.getTicketClassId());

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void findAvailable_ShouldReturnOnlyAvailableClasses() {
        // Arrange
        FlightTicketClass unavailableFlightTicketClass = new FlightTicketClass();
        unavailableFlightTicketClass.setFlightId(testFlight.getFlightId());
        unavailableFlightTicketClass.setTicketClassId(testTicketClass.getTicketClassId());
        unavailableFlightTicketClass.setFlight(testFlight);
        unavailableFlightTicketClass.setTicketClass(testTicketClass);
        unavailableFlightTicketClass.setTicketQuantity(50);
        unavailableFlightTicketClass.setRemainingTicketQuantity(0); // No remaining tickets
        unavailableFlightTicketClass.setSpecifiedFare(new BigDecimal("199.99"));
        entityManager.persistAndFlush(unavailableFlightTicketClass);

        // Act
        List<FlightTicketClass> result = flightTicketClassRepository.findAvailable();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRemainingTicketQuantity() > 0);
    }

    @Test
    void findAvailableForFutureFlights_ShouldReturnOnlyFutureAvailableFlights() {
        // Arrange
        Flight pastFlight = new Flight();
        pastFlight.setFlightCode("CC789");
        pastFlight.setDepartureTime(LocalDateTime.now().minusDays(1)); // Past flight
        pastFlight.setArrivalTime(LocalDateTime.now().minusDays(1).plusHours(2));
        pastFlight = entityManager.persistAndFlush(pastFlight);

        FlightTicketClass pastFlightTicketClass = new FlightTicketClass();
        pastFlightTicketClass.setFlightId(pastFlight.getFlightId());
        pastFlightTicketClass.setTicketClassId(testTicketClass.getTicketClassId());
        pastFlightTicketClass.setFlight(pastFlight);
        pastFlightTicketClass.setTicketClass(testTicketClass);
        pastFlightTicketClass.setTicketQuantity(50);
        pastFlightTicketClass.setRemainingTicketQuantity(20);
        pastFlightTicketClass.setSpecifiedFare(new BigDecimal("249.99"));
        entityManager.persistAndFlush(pastFlightTicketClass);

        // Act
        List<FlightTicketClass> result = flightTicketClassRepository.findAvailableForFutureFlights();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getFlight().getDepartureTime().isAfter(LocalDateTime.now()));
        assertTrue(result.get(0).getRemainingTicketQuantity() > 0);
    }
}
