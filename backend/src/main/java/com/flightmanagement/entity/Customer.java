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
@Table(name = "customer")
public class Customer {
    
    @Id
    @Column(name = "customer_id")
    private Integer customerId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id")
    private Account account;
    
    @Column(name = "score")
    private Integer score = 0;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
