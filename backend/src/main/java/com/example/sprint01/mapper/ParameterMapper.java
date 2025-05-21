package com.example.sprint01.mapper;

import com.example.sprint01.dto.ParameterDto;
import com.example.sprint01.entity.Parameter;

public class ParameterMapper {
    public static ParameterDto mapToDto(Parameter parameter) {
        ParameterDto parameterDto = new ParameterDto();
        parameterDto.setMaxMediumAirport(parameter.getMaxMediumAirport());
        parameterDto.setMinFlightDuration(parameter.getMinFlightDuration());
        parameterDto.setMaxFlightDuration(parameter.getMaxFlightDuration());
        parameterDto.setMaxStopDuration(parameter.getMaxStopDuration());
        return parameterDto;
    }

    public static Parameter mapToEntity(ParameterDto parameterDto) {
        Parameter parameter = new Parameter();
        parameter.setMaxMediumAirport(parameterDto.getMaxMediumAirport());
        parameter.setMinFlightDuration(parameterDto.getMinFlightDuration());
        parameter.setMaxFlightDuration(parameterDto.getMaxFlightDuration());
        parameter.setMaxStopDuration(parameterDto.getMaxStopDuration());
        return parameter;
    }
}
