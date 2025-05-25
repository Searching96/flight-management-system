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
@Table(name = "flight")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;
    
    @ManyToOne
    @JoinColumn(name = "plane_id", nullable = false)
    private Plane plane;
    
    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;
    
    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;
    
    @Column(name = "flight_code", nullable = false, length = 200)
    private String flightCode;
    
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
