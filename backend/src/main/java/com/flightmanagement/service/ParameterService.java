package com.flightmanagement.service;

import com.flightmanagement.dto.ParameterDto;

public interface ParameterService {
    ParameterDto getLatestParameter();
    
    ParameterDto updateParameters(ParameterDto parameterDto);

    void updateMaxMediumAirports(int maxMediumAirports);

    void updateMinFlightDuration(int minFlightDuration);

    void updateMaxLayoverDuration(int maxLayoverDuration);
    
    void updateMinLayoverDuration(int minLayoverDuration);
    
    void updateMinBookingInAdvanceDuration(int minBookingInAdvanceDuration);
    
    void updateMaxBookingHoldDuration(int maxBookingHoldDuration);
    
    void initializeDefaultParameters();
}
