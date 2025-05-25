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
@Table(name = "ticket_class")
public class TicketClass {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_class_id")
    private Integer ticketClassId;
    
    @Column(name = "ticket_class_name", nullable = false, length = 200)
    private String ticketClassName;
    
    @Column(name = "color", nullable = false, length = 200)
    private String color;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
