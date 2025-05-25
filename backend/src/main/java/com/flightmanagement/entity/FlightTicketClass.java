package com.flightmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.flightmanagement.entity.composite.FlightTicketClassId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight_ticket_class")
@IdClass(FlightTicketClassId.class)
public class FlightTicketClass {
    
    @Id
    @Column(name = "flight_id")
    private Integer flightId;
    
    @Id
    @Column(name = "ticket_class_id")
    private Integer ticketClassId;
    
    @ManyToOne
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight;
    
    @ManyToOne
    @JoinColumn(name = "ticket_class_id", insertable = false, updatable = false)
    private TicketClass ticketClass;
    
    @Column(name = "ticket_quantity", nullable = false)
    private Integer ticketQuantity;
    
    @Column(name = "remaining_ticket_quantity", nullable = false)
    private Integer remainingTicketQuantity;
    
    @Column(name = "specified_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal specifiedFare;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
