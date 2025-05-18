package com.example.sprint01.controller;

import com.example.sprint01.service.ParameterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("api/parameters")
public class ParameterController {
    private ParameterService parameterService;

    @GetMapping("/max-medium-airport")
    public ResponseEntity<Integer> getMaxMediumAirport() {
        return ResponseEntity.ok(parameterService.getMaxMediumAirports());
    }

    @GetMapping("/min-flight-duration")
    public ResponseEntity<Integer> getMinFlightDuration() {
        return ResponseEntity.ok(parameterService.getMinFlightDuration());
    }

    @GetMapping("/max-flight-duration")
    public ResponseEntity<Integer> getMaxFlightDuration() {
        return ResponseEntity.ok(parameterService.getMaxFlightDuration());
    }

    @GetMapping("/max-stop-duration")
    public ResponseEntity<Integer> getMaxStopDuration() {
        return ResponseEntity.ok(parameterService.getMaxStopDuration());
    }

    @PutMapping("/max-medium-airport")
    public ResponseEntity<String> updateMaxMediumAirport(@RequestParam Integer maxMediumAirport) {
        parameterService.updateMaxMediumAirports(maxMediumAirport);
        return ResponseEntity.ok("Max medium airport updated successfully");
    }

    @PutMapping("/min-flight-duration")
    public ResponseEntity<String> updateMinFlightDuration(@RequestParam Integer minFlightDuration) {
        parameterService.updateMinFlightDuration(minFlightDuration);
        return ResponseEntity.ok("Min flight duration updated successfully");
    }

    @PutMapping("/max-flight-duration")
    public ResponseEntity<String> updateMaxFlightDuration(@RequestParam Integer maxFlightDuration) {
        parameterService.updateMaxFlightDuration(maxFlightDuration);
        return ResponseEntity.ok("Max flight duration updated successfully");
    }

    @PutMapping("/max-stop-duration")
    public ResponseEntity<String> updateMaxStopDuration(@RequestParam Integer maxStopDuration) {
        parameterService.updateMaxStopDuration(maxStopDuration);
        return ResponseEntity.ok("Max stop duration updated successfully");
    }
}
