package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirportDto {
    
    private Integer airportId;
    private String airportName;
    private String cityName;
    private String countryName;
}
