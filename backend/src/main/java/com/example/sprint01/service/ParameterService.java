package com.example.sprint01.service;

import com.example.sprint01.dto.ParameterDto;

public interface ParameterService {
    ParameterDto getParameterSet();

    void updateMaxMediumAirports(int maxMediumAirports);

    void updateMinFlightDuration(int minFlightDuration);

    void updateMaxFlightDuration(int maxFlightDuration);

    void updateMaxStopDuration(int maxStopDuration);
}
