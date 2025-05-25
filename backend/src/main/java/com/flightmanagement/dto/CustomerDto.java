package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    
    private Integer customerId;
    private String accountName;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer score;
}
