package com.flightmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDetailId implements Serializable {
    
    private Integer flightId;
    private Integer mediumAirportId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDetailId that = (FlightDetailId) o;
        return Objects.equals(flightId, that.flightId) && 
               Objects.equals(mediumAirportId, that.mediumAirportId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(flightId, mediumAirportId);
    }
}
