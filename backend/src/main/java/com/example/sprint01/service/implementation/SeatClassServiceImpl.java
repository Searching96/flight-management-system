package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.SeatClassDto;
import com.example.sprint01.entity.SeatClass;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.SeatClassMapper;
import com.example.sprint01.repository.SeatClassRepository;
import com.example.sprint01.service.SeatClassService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SeatClassServiceImpl implements SeatClassService {
    private SeatClassRepository seatClassRepository;

    @Override
    public SeatClassDto createAirport(SeatClassDto seatClassDto) {
        SeatClass seatClass = SeatClassMapper.mapToSeatClass(seatClassDto);
        SeatClass savedSeatClass = seatClassRepository.save(seatClass);
        return SeatClassMapper.mapToDto(savedSeatClass);
    }

    @Override
    public SeatClassDto updateAirport(Long id, SeatClassDto updatedSeatClass) {
        SeatClass existingSeatClass = seatClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat class not found with id: " + id));

        existingSeatClass.setSeatName(updatedSeatClass.getSeatName());
        SeatClass updatedSeatClassObj = seatClassRepository.save(existingSeatClass);

        return SeatClassMapper.mapToDto(updatedSeatClassObj);
    }

    @Override
    public void deleteAirport(Long id) {
        SeatClass existingSeatClass = seatClassRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat class not found with id: " + id));

        // Use the current timestamp
        existingSeatClass.setDeletedAt(LocalDateTime.now());
        seatClassRepository.save(existingSeatClass);
    }

    @Override
    public SeatClassDto getAirportById(Long id) {
        SeatClass seatClass = seatClassRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat class not found with id: " + id));
        return SeatClassMapper.mapToDto(seatClass);
    }

    @Override
    public List<SeatClassDto> getAllAirports() {
        List<SeatClass> seatClasses = seatClassRepository.findAllActive();
        return seatClasses.stream()
                .map(SeatClassMapper::mapToDto)
                .toList();
    }
}