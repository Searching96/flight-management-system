package com.flightmanagement.repository;

import com.flightmanagement.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL")
    List<Ticket> findAllActive();

    @Query("SELECT t FROM Ticket t WHERE t.ticketId = ?1 AND t.deletedAt IS NULL")
    Optional<Ticket> findActiveById(Integer id);

    @Query("SELECT t FROM Ticket t WHERE t.flight.flightId = ?1 AND t.deletedAt IS NULL")
    List<Ticket> findByFlightId(Integer flightId);

    @Query("SELECT t FROM Ticket t WHERE t.bookCustomer.customerId = ?1 AND t.deletedAt IS NULL")
    List<Ticket> findByCustomerId(Integer customerId);

    @Query("SELECT t FROM Ticket t WHERE t.passenger.passengerId = ?1 AND t.deletedAt IS NULL")
    List<Ticket> findByPassengerId(Integer passengerId);

    @Query("SELECT t FROM Ticket t WHERE t.ticketStatus = ?1 AND t.deletedAt IS NULL")
    List<Ticket> findByTicketStatus(Byte ticketStatus);

    @Query("SELECT t FROM Ticket t WHERE t.flight.flightId = ?1 AND t.seatNumber = ?2 AND t.deletedAt IS NULL")
    Optional<Ticket> findByFlightIdAndSeatNumber(Integer flightId, String seatNumber);

    @Query("SELECT t FROM Ticket t WHERE t.confirmationCode = ?1 AND t.deletedAt IS NULL")
    List<Ticket> findByConfirmationCode(String confirmationCode);

    @Query("SELECT t FROM Ticket t WHERE t.ticketStatus = 0 " +
            "AND t.flight.departureTime <= :cutoffTime " +
            "AND t.deletedAt IS NULL")
    List<Ticket> findExpiredUnpaidTickets(@Param("cutoffTime") LocalDateTime cutoffTime);
}
