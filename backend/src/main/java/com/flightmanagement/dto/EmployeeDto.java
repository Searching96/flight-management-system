package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Integer employeeId;
    private String accountName;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer employeeType;
    private String employeeTypeName;
    private LocalDateTime deletedAt;
}
