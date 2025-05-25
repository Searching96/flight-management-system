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
        Parameter parameter = parameterMapper.toEntity(parameterDto);
        parameter.setDeletedAt(null);
        Parameter savedParameter = parameterRepository.save(parameter);
        return parameterMapper.toDto(savedParameter);
    }
    
    @Override
    public void updateMaxMediumAirports(int maxMediumAirports) {
        Parameter parameter = getCurrentParameter();
        parameter.setMaxMediumAirport(maxMediumAirports);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void updateMinFlightDuration(int minFlightDuration) {
        Parameter parameter = getCurrentParameter();
        parameter.setMinFlightDuration(minFlightDuration);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void updateMaxLayoverDuration(int maxLayoverDuration) {
        Parameter parameter = getCurrentParameter();
        parameter.setMaxLayoverDuration(maxLayoverDuration);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void updateMinLayoverDuration(int minLayoverDuration) {
        Parameter parameter = getCurrentParameter();
        parameter.setMinLayoverDuration(minLayoverDuration);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void updateMinBookingInAdvanceDuration(int minBookingInAdvanceDuration) {
        Parameter parameter = getCurrentParameter();
        parameter.setMinBookingInAdvanceDuration(minBookingInAdvanceDuration);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void updateMaxBookingHoldDuration(int maxBookingHoldDuration) {
        Parameter parameter = getCurrentParameter();
        parameter.setMaxBookingHoldDuration(maxBookingHoldDuration);
        parameterRepository.save(parameter);
    }
    
    @Override
    public void initializeDefaultParameters() {
        Parameter parameter = new Parameter();
        parameter.setMaxMediumAirport(2);
        parameter.setMinFlightDuration(30);
        parameter.setMinLayoverDuration(30);
        parameter.setMaxLayoverDuration(720);
        parameter.setMinBookingInAdvanceDuration(4);
        parameter.setMaxBookingHoldDuration(24);
        parameter.setDeletedAt(null);
        parameterRepository.save(parameter);
    }
    
    private Parameter getCurrentParameter() {
        return parameterRepository.findLatestParameter()
            .orElseThrow(() -> new RuntimeException("No parameters found"));
    }
}
