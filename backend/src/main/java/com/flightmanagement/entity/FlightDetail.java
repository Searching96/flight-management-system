package com.flightmanagement.entity;

import jakarta.persistence.*;
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
@Table(name = "flight_detail")
@IdClass(FlightDetailId.class)
public class FlightDetail {
    
    @Id
    @Column(name = "flight_id")
    private Integer flightId;
    
    @Id
    @Column(name = "medium_airport_id")
    private Integer mediumAirportId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medium_airport_id", insertable = false, updatable = false)
    private Airport mediumAirport;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "layover_duration", nullable = false)
    private Integer layoverDuration; // in minutes
    
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class FlightDetailId implements java.io.Serializable {
    
    private Integer flightId;
    private Integer mediumAirportId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDetailId that = (FlightDetailId) o;
        return java.util.Objects.equals(flightId, that.flightId) && 
               java.util.Objects.equals(mediumAirportId, that.mediumAirportId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(flightId, mediumAirportId);
    }
}