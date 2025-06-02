package com.flightmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatbox", 
       uniqueConstraints = @UniqueConstraint(name = "unique_chat_box", 
       columnNames = {"customer_id", "employee_id"}))
public class Chatbox {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatbox_id")
    private Integer chatboxId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "chatbox", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}
