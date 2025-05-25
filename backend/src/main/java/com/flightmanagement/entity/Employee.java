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
@Table(name = "employee")
public class Employee {
    
    @Id
    @Column(name = "employee_id")
    private Integer employeeId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private Account account;
    
    @Column(name = "employee_type", nullable = false)
    private Integer employeeType; // 1: tiep nhan lich bay, 2: ban/dat ve, 3: cskh, 4: ke toan, 5: sa
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
