package com.flightmanagement.mapper;

import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketMapper implements BaseMapper<Ticket, TicketDto> {

    @Override
    public TicketDto toDto(Ticket entity) {
        if (entity == null)
            return null;

        TicketDto dto = new TicketDto();
        dto.setTicketId(entity.getTicketId());
        dto.setSeatNumber(entity.getSeatNumber());
        dto.setTicketStatus(entity.getTicketStatus());
        dto.setPaymentTime(entity.getPaymentTime());
        dto.setFare(entity.getFare());
        dto.setConfirmationCode(entity.getConfirmationCode());
        dto.setOrderId(entity.getOrderId());

        if (entity.getFlight() != null) {
            dto.setFlightId(entity.getFlight().getFlightId());
        }

        if (entity.getTicketClass() != null) {
            dto.setTicketClassId(entity.getTicketClass().getTicketClassId());
        }

        if (entity.getBookCustomer() != null) {
            dto.setBookCustomerId(entity.getBookCustomer().getCustomerId());
        }

        if (entity.getPassenger() != null) {
            dto.setPassengerId(entity.getPassenger().getPassengerId());
        }

        return dto;
    }

    @Override
    public Ticket toEntity(TicketDto dto) {
        if (dto == null)
            return null;

        Ticket entity = new Ticket();
        entity.setTicketId(dto.getTicketId());
        entity.setSeatNumber(dto.getSeatNumber());
        entity.setTicketStatus(dto.getTicketStatus());
        entity.setPaymentTime(dto.getPaymentTime());
        entity.setFare(dto.getFare());
        entity.setConfirmationCode(dto.getConfirmationCode());

        return entity;
    }

    @Override
    public List<TicketDto> toDtoList(List<Ticket> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Ticket> toEntityList(List<TicketDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}