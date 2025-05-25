package com.flightmanagement.entity.composite;

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
public class FlightTicketClassId implements Serializable {
    
    private Integer flightId;
    private Integer ticketClassId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightTicketClassId that = (FlightTicketClassId) o;
        return Objects.equals(flightId, that.flightId) && 
               Objects.equals(ticketClassId, that.ticketClassId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(flightId, ticketClassId);
    }
}
