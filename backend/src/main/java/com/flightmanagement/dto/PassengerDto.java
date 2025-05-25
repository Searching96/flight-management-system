package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {
    
    private Integer passengerId;
    private String passengerName;
    private String email;
    private String citizenId;
    private String phoneNumber;
}
