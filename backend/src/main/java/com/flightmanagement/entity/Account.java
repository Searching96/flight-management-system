package com.flightmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "account")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;
    
    @NotBlank(message = "Account name is required")
    @Size(max = 255, message = "Account name must not exceed 255 characters")
    @Column(name = "account_name", nullable = false)
    private String accountName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Citizen ID is required")
    @Size(max = 20, message = "Citizen ID must not exceed 20 characters")
    @Column(name = "citizen_id", nullable = false, unique = true)
    private String citizenId;
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @NotNull(message = "Account type is required")
    @Column(name = "account_type", nullable = false)
    private Integer accountType; // 1: customer, 2: employee

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Employee employee;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Customer customer;
}
