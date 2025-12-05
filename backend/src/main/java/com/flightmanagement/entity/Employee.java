package com.flightmanagement.entity;

import com.flightmanagement.enums.EmployeeType;
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

    private EmployeeType employeeType;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
