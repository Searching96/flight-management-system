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
@Table(name = "message")
public class Message {
    
    @Id
    @Column(name = "message_id")
    private Integer messageId;
    
    @ManyToOne
    @JoinColumn(name = "chatbox_id", nullable = false)
    private Chatbox chatbox;
    
    @Column(name = "message_type", nullable = false)
    private Integer messageType; // 1: customer send to employee, 2: employee send to customer
    
    @Column(name = "content", nullable = false, length = 1000000)
    private String content;
    
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
