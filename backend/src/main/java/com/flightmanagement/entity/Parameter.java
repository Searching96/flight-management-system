package com.flightmanagement.entity;

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
@Table(name = "parameter")
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "max_medium_airport", nullable = false)
    private Integer maxMediumAirport;
    
    @Column(name = "min_flight_duration", nullable = false)
    private Integer minFlightDuration;
    
    @Column(name = "min_layover_duration", nullable = false)
    private Integer minLayoverDuration;
    
    @Column(name = "max_layover_duration", nullable = false)
    private Integer maxLayoverDuration;
    
    @Column(name = "min_booking_in_advance_duration", nullable = false)
    private Integer minBookingInAdvanceDuration;
    
    @Column(name = "max_booking_hold_duration", nullable = false)
    private Integer maxBookingHoldDuration;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
