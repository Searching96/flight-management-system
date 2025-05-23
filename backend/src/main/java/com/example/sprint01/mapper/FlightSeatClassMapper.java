package com.example.sprint01.mapper;

import com.example.sprint01.dto.FlightSeatClassDto;
import com.example.sprint01.entity.FlightSeatClass;
import com.example.sprint01.entity.composite_key.FlightSeatClassId;

public class FlightSeatClassMapper {
    public static FlightSeatClassDto mapToDto(FlightSeatClass FlightSeatClass) {
        return new FlightSeatClassDto(
                FlightSeatClass.getId().getFlightId(),
                FlightSeatClass.getId().getSeatClassId(),
                FlightSeatClass.getTotalTickets(),
                FlightSeatClass.getRemainingTickets(),
                FlightSeatClass.getCurrentPrice()
        );
    }

    public static FlightSeatClass mapToFlightSeatClass(FlightSeatClassDto FlightSeatClassDto) {
        return new FlightSeatClass(
                new FlightSeatClassId(FlightSeatClassDto.getFlightId(), FlightSeatClassDto.getSeatClassId()),
                FlightSeatClassDto.getTotalTickets(),
                FlightSeatClassDto.getRemainingTickets(),
                FlightSeatClassDto.getCurrentPrice(),
                null,
                null,
                null
        );
    }
}
