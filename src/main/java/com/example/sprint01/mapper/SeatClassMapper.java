package com.example.sprint01.mapper;

import com.example.sprint01.dto.SeatClassDto;
import com.example.sprint01.entity.SeatClass;

public class SeatClassMapper {
    public static SeatClassDto mapToDto(SeatClass seatClass) {
        return new SeatClassDto(
                seatClass.getId(),
                seatClass.getSeatName()
        );
    }

    public static SeatClass mapToSeatClass(SeatClassDto seatClassDtoDto) {
        return new SeatClass(
                seatClassDtoDto.getId(),
                seatClassDtoDto.getSeatName()
        );
    }
}
