package com.example.sprint01.entity;

import com.example.sprint01.entity.composite_key.FlightSeatClassId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight_seat_classes")
public class FlightSeatClass {
    @EmbeddedId
    private FlightSeatClassId id;

    @Column(name = "total_tickets", nullable = false)
    private int totalTickets;

    @Column(name = "remaining_tickets", nullable = false)
    private int remainingTickets;

    @Column(name = "current_price", nullable = false)
    private int currentPrice;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @MapsId("seatClassId")
    @JoinColumn(name = "seat_class_id", nullable = false)
    private SeatClass seatClass;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}