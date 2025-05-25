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
@Table(name = "passenger")
public class Passenger {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Integer passengerId;
    
    @Column(name = "passenger_name", nullable = false, length = 200)
    private String passengerName;
    
    @Column(name = "email", nullable = false, length = 200)
    private String email;
    
    @Column(name = "citizen_id", nullable = false, unique = true, length = 200)
    private String citizenId;
    
    @Column(name = "phone_number", nullable = false, length = 200)
    private String phoneNumber;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
