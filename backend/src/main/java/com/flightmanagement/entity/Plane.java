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
@Table(name = "plane")
public class Plane {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plane_id")
    private Integer planeId;
    
    @Column(name = "plane_code", nullable = false, length = 200)
    private String planeCode;
    
    @Column(name = "plane_type", nullable = false, length = 200)
    private String planeType;
    
    @Column(name = "seat_quantity", nullable = false)
    private Integer seatQuantity;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
