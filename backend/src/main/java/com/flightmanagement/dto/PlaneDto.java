package com.flightmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaneDto {
    
    private Integer planeId;
    private String planeCode;
    private String planeType;
    private Integer seatQuantity;
}
