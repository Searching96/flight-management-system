package com.flightmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;
    
    @Column(name = "chatbox_id", nullable = false)
    private Integer chatboxId;
    
    @ManyToOne
    @JoinColumn(name = "chatbox_id", insertable = false, updatable = false)
    private Chatbox chatbox;
    
    @Column(name = "employee_id")
    private Integer employeeId;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
