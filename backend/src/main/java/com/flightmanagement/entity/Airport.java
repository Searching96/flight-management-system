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
@Table(name = "airport")
public class Airport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airport_id")
    private Integer airportId;
    
    @Column(name = "airport_name", nullable = false, length = 200)
    private String airportName;
    
    @Column(name = "city_name", nullable = false, length = 200)
    private String cityName;
    
    @Column(name = "country_name", nullable = false, length = 200)
    private String countryName;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
