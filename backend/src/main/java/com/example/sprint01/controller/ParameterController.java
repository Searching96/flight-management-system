package com.example.sprint01.controller;

import com.example.sprint01.service.ParameterService;
import com.example.sprint01.dto.ParameterDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/parameters")
public class ParameterController {
    private ParameterService parameterService;

    @GetMapping("")
    public ResponseEntity<ParameterDto> getParameter() {
        ParameterDto parameters = parameterService.getParameterSet();
        return ResponseEntity.ok(parameters);
    }

    @PutMapping("")
    public ResponseEntity<String> updateParameter(@RequestBody ParameterDto dto) {
        if (dto.getMaxMediumAirport() != null) {
            parameterService.updateMaxMediumAirports(dto.getMaxMediumAirport());
        }
        if (dto.getMinFlightDuration() != null) {
            parameterService.updateMinFlightDuration(dto.getMinFlightDuration());
        }
        if (dto.getMaxFlightDuration() != null) {
            parameterService.updateMaxFlightDuration(dto.getMaxFlightDuration());
        }
        if (dto.getMaxStopDuration() != null) {
            parameterService.updateMaxStopDuration(dto.getMaxStopDuration());
        }
        return ResponseEntity.ok("Parameters updated successfully");
    }
}

