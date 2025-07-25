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
@Table(name = "ticket")
public class Ticket {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;
    
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
    
    @ManyToOne
    @JoinColumn(name = "ticket_class_id", nullable = false)
    private TicketClass ticketClass;
    
    @ManyToOne
    @JoinColumn(name = "book_customer_id")
    private Customer bookCustomer;
    
    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;
    
    @Column(name = "seat_number", nullable = false, length = 7)
    private String seatNumber;
    
    @Column(name = "ticket_status")
    private Byte ticketStatus = 0; // 1: paid, 0: unpaid
    
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    @Column(name = "fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(name = "confirmation_code", nullable = false, length = 20)
    private String confirmationCode;

    @Column(name = "order_id", nullable = true, length = 100)
    private String orderId;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
