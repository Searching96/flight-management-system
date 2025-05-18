package com.example.sprint01.entity;

import com.example.sprint01.entity.composite_key.FlightDetailsId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight_details")
public class FlightDetails {
    @EmbeddedId
    private FlightDetailsId id;

    @Column(name = "stop_time", nullable = false)
    private int stopTime;

    @Column(name="note", length = 255)
    private String note;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @MapsId("mediumAirportId")
    @JoinColumn(name = "medium_airport_id", nullable = false)
    private Airport mediumAirport;
}
