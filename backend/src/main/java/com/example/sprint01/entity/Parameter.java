package com.example.sprint01.entity;

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
@Table(name = "parameters")
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_medium_airport", nullable = false)
    private int maxMediumAirport;

    @Column(name = "min_flight_duration", nullable = false)
    private int minFlightDuration;

    @Column(name = "max_flight_duration", nullable = false)
    private int maxFlightDuration;

    @Column(name = "max_stop_duration", nullable = false)
    private int maxStopDuration;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}