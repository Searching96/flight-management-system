package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Parameter;
import com.flightmanagement.mapper.ParameterMapper;
import com.flightmanagement.repository.ParameterRepository;
import com.flightmanagement.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParameterServiceImpl implements ParameterService {
    
    @Autowired
    private ParameterRepository parameterRepository;
    
    @Autowired
    private ParameterMapper parameterMapper;
    
    @Override
    public ParameterDto getParameterSet() {
        Parameter parameter = parameterRepository.findLatestParameter()
            .orElseThrow(() -> new RuntimeException("No parameters found"));
        return parameterMapper.toDto(parameter);
    }
    
    @Override
    public ParameterDto updateParameters(ParameterDto parameterDto) {
        // Delete all existing parameters first
        deleteAllExistingParameters();
        
        // Create new parameter record
        Parameter parameter = parameterMapper.toEntity(parameterDto);
        parameter.setId(null); // Ensure new record is created
        parameter.setDeletedAt(null);
        Parameter savedParameter = parameterRepository.save(parameter);
        return parameterMapper.toDto(savedParameter);
    }
    
    @Override
    public void updateMaxMediumAirports(int maxMediumAirports) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMaxMediumAirport(maxMediumAirports);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinFlightDuration(int minFlightDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMinFlightDuration(minFlightDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMaxLayoverDuration(int maxLayoverDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMaxLayoverDuration(maxLayoverDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinLayoverDuration(int minLayoverDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMinLayoverDuration(minLayoverDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinBookingInAdvanceDuration(int minBookingInAdvanceDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMinBookingInAdvanceDuration(minBookingInAdvanceDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMaxBookingHoldDuration(int maxBookingHoldDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getParameterSet();
        currentParams.setMaxBookingHoldDuration(maxBookingHoldDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void initializeDefaultParameters() {
        // Delete all existing parameters first
        deleteAllExistingParameters();
        
        // Create new default parameter record
        Parameter parameter = new Parameter();
        parameter.setMaxMediumAirport(2);
        parameter.setMinFlightDuration(30);
        parameter.setMinLayoverDuration(30);
        parameter.setMaxLayoverDuration(720);
        parameter.setMinBookingInAdvanceDuration(1);
        parameter.setMaxBookingHoldDuration(24);
        parameter.setDeletedAt(null);
        parameterRepository.save(parameter);
    }
    
    private void deleteAllExistingParameters() {
        // Hard delete all existing parameter records
        parameterRepository.deleteAll();
    }
}
