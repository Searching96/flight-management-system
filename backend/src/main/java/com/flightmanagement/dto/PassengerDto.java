package com.flightmanagement.dto;

import lombok.*;

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
