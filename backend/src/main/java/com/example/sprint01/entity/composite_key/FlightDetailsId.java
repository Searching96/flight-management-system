package com.example.sprint01.entity.composite_key;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class FlightDetailsId implements Serializable {
    private Long flightId;
    private Long mediumAirportId;
}
