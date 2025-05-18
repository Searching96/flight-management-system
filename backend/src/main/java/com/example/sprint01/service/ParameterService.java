package com.example.sprint01.service;

import com.example.sprint01.entity.Parameter;

public interface ParameterService {
    Parameter getParameterSet();

    int getMaxMediumAirports();

    int getMinFlightDuration();

    int getMaxFlightDuration();

    int getMaxStopDuration();

    void updateMaxMediumAirports(int maxMediumAirports);

    void updateMinFlightDuration(int minFlightDuration);

    void updateMaxFlightDuration(int maxFlightDuration);

    void updateMaxStopDuration(int maxStopDuration);
}
