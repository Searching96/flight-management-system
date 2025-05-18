package com.example.sprint01.service.implementation;

import com.example.sprint01.entity.Parameter;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.repository.ParameterRepository;
import com.example.sprint01.service.ParameterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private ParameterRepository parameterRepository;

    private static final Long DEFAULT_ID = 1L;

    @Override
    public Parameter getParameterSet() {
        return parameterRepository.findById(DEFAULT_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter set not found"));
    }

    @Override
    public int getMaxMediumAirports() {
        return getParameterSet().getMaxMediumAirport();
    }

    @Override
    public int getMinFlightDuration() {
        return getParameterSet().getMinFlightDuration();
    }

    @Override
    public int getMaxFlightDuration() {
        return getParameterSet().getMaxFlightDuration();
    }

    @Override
    public int getMaxStopDuration() {
        return getParameterSet().getMaxStopDuration();
    }

    @Override
    public void updateMaxMediumAirports(int maxMediumAirports) {
        Parameter parameter = getParameterSet();
        parameter.setMaxMediumAirport(maxMediumAirports);
        parameterRepository.save(parameter);
    }

    @Override
    public void updateMinFlightDuration(int minFlightDuration) {
        Parameter parameter = getParameterSet();
        parameter.setMinFlightDuration(minFlightDuration);
        parameterRepository.save(parameter);
    }

    @Override
    public void updateMaxFlightDuration(int maxFlightDuration) {
        Parameter parameter = getParameterSet();
        parameter.setMaxFlightDuration(maxFlightDuration);
        parameterRepository.save(parameter);
    }

    @Override
    public void updateMaxStopDuration(int maxStopDuration) {
        Parameter parameter = getParameterSet();
        parameter.setMaxStopDuration(maxStopDuration);
        parameterRepository.save(parameter);
    }
}
