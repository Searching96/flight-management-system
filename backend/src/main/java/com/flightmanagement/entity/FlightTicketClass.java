package com.flightmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    @Column(name = "ticket_quantity")
    private Integer ticketQuantity;
    
    @Column(name = "remaining_ticket_quantity")
    private Integer remainingTicketQuantity;
    
    @Column(name = "specified_fare", precision = 10, scale = 2)
    private BigDecimal specifiedFare;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}

// Composite key class
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class FlightTicketClassId implements java.io.Serializable {
    private Integer flightId;
    private Integer ticketClassId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightTicketClassId that = (FlightTicketClassId) o;
        return java.util.Objects.equals(flightId, that.flightId) && 
               java.util.Objects.equals(ticketClassId, that.ticketClassId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(flightId, ticketClassId);
    }
}