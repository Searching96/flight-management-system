package com.example.sprint01.service.implementation;

import com.example.sprint01.dto.ParameterDto;
import com.example.sprint01.entity.Parameter;
import com.example.sprint01.exception.ResourceNotFoundException;
import com.example.sprint01.mapper.ParameterMapper;
import com.example.sprint01.repository.ParameterRepository;
import com.example.sprint01.service.ParameterService;
import lombok.AllArgsConstructor;
import org.apache.catalina.util.ParameterMap;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private ParameterRepository parameterRepository;

    private static final Long DEFAULT_ID = 1L;

    @Override
    public ParameterDto getParameterSet() {
        Parameter parameter = parameterRepository.findById(DEFAULT_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter set not found"));
        return ParameterMapper.mapToDto(parameter);
    }

    @Override
    public void updateMaxMediumAirports(int maxMediumAirports) {
        ParameterDto parameter = getParameterSet();
        parameter.setMaxMediumAirport(maxMediumAirports);
        parameterRepository.save(ParameterMapper.mapToEntity(parameter));
    }

    @Override
    public void updateMinFlightDuration(int minFlightDuration) {
        ParameterDto parameter = getParameterSet();
        parameter.setMinFlightDuration(minFlightDuration);
        parameterRepository.save(ParameterMapper.mapToEntity(parameter));
    }

    @Override
    public void updateMaxFlightDuration(int maxFlightDuration) {
        ParameterDto parameter = getParameterSet();
        parameter.setMaxFlightDuration(maxFlightDuration);
        parameterRepository.save(ParameterMapper.mapToEntity(parameter));
    }

    @Override
    public void updateMaxStopDuration(int maxStopDuration) {
        ParameterDto parameter = getParameterSet();
        parameter.setMaxStopDuration(maxStopDuration);
        parameterRepository.save(ParameterMapper.mapToEntity(parameter));
    }
}
