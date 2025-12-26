package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Parameter;
import com.flightmanagement.mapper.ParameterMapper;
import com.flightmanagement.repository.ParameterRepository;
import com.flightmanagement.service.ParameterService;
import com.flightmanagement.service.AuditLogService;
import org.springframework.stereotype.Service;

@Service
public class ParameterServiceImpl implements ParameterService {
    
    private final ParameterRepository parameterRepository;
    
    private final ParameterMapper parameterMapper;

    private final AuditLogService auditLogService;

    public ParameterServiceImpl(ParameterRepository parameterRepository, ParameterMapper parameterMapper, AuditLogService auditLogService) {
        this.parameterRepository = parameterRepository;
        this.parameterMapper = parameterMapper;
        this.auditLogService = auditLogService;
    }
    
    @Override
    public ParameterDto getLatestParameter() {
        Parameter parameter = parameterRepository.findLatestParameter()
            .orElseThrow(() -> new RuntimeException("No parameters found"));
        return parameterMapper.toDto(parameter);
    }
    
    @Override
    public ParameterDto updateParameters(ParameterDto parameterDto) {
        // Get old values for audit
        ParameterDto oldParams = getLatestParameter();
        
        // Delete all existing parameters first
        deleteAllExistingParameters();
        
        // Create new parameter record
        Parameter parameter = parameterMapper.toEntity(parameterDto);
        parameter.setId(null); // Ensure new record is created
        parameter.setDeletedAt(null);
        Parameter savedParameter = parameterRepository.save(parameter);
        
        // Audit log all changes
        if (!oldParams.getMaxMediumAirport().equals(parameterDto.getMaxMediumAirport())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "maxMediumAirport", oldParams.getMaxMediumAirport().toString(), parameterDto.getMaxMediumAirport().toString(), "system");
        }
        if (!oldParams.getMinFlightDuration().equals(parameterDto.getMinFlightDuration())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "minFlightDuration", oldParams.getMinFlightDuration().toString(), parameterDto.getMinFlightDuration().toString(), "system");
        }
        if (!oldParams.getMinLayoverDuration().equals(parameterDto.getMinLayoverDuration())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "minLayoverDuration", oldParams.getMinLayoverDuration().toString(), parameterDto.getMinLayoverDuration().toString(), "system");
        }
        if (!oldParams.getMaxLayoverDuration().equals(parameterDto.getMaxLayoverDuration())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "maxLayoverDuration", oldParams.getMaxLayoverDuration().toString(), parameterDto.getMaxLayoverDuration().toString(), "system");
        }
        if (!oldParams.getMinBookingInAdvanceDuration().equals(parameterDto.getMinBookingInAdvanceDuration())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "minBookingInAdvanceDuration", oldParams.getMinBookingInAdvanceDuration().toString(), parameterDto.getMinBookingInAdvanceDuration().toString(), "system");
        }
        if (!oldParams.getMaxBookingHoldDuration().equals(parameterDto.getMaxBookingHoldDuration())) {
            auditLogService.saveAuditLog("Parameter", savedParameter.getId().toString(), "UPDATE", "maxBookingHoldDuration", oldParams.getMaxBookingHoldDuration().toString(), parameterDto.getMaxBookingHoldDuration().toString(), "system");
        }
        
        return parameterMapper.toDto(savedParameter);
    }
    
    @Override
    public void updateMaxMediumAirports(int maxMediumAirports) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
        currentParams.setMaxMediumAirport(maxMediumAirports);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinFlightDuration(int minFlightDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
        currentParams.setMinFlightDuration(minFlightDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMaxLayoverDuration(int maxLayoverDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
        currentParams.setMaxLayoverDuration(maxLayoverDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinLayoverDuration(int minLayoverDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
        currentParams.setMinLayoverDuration(minLayoverDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMinBookingInAdvanceDuration(int minBookingInAdvanceDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
        currentParams.setMinBookingInAdvanceDuration(minBookingInAdvanceDuration);
        updateParameters(currentParams);
    }
    
    @Override
    public void updateMaxBookingHoldDuration(int maxBookingHoldDuration) {
        // Delete all existing parameters and create new one
        ParameterDto currentParams = getLatestParameter();
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
