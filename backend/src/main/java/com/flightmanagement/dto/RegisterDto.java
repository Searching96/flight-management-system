package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String accountName;
    private String email;
    private String password;
    private String citizenId;
    private String phoneNumber;
    private Integer accountType;
    private Integer employeeType;
}

