package com.flightmanagement.mapper;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.EmployeeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setAccountName(entity.getAccount() != null ? entity.getAccount().getAccountName() : null);
        dto.setEmail(entity.getAccount() != null ? entity.getAccount().getEmail() : null);
        dto.setPhoneNumber(entity.getAccount() != null ? entity.getAccount().getPhoneNumber() : null);
        dto.setCitizenId(entity.getAccount() != null ? entity.getAccount().getCitizenId() : null);
        dto.setEmployeeType(entity.getEmployeeType() != null ? entity.getEmployeeType().getValue() : null);
        dto.setEmployeeTypeName(getEmployeeTypeName(entity.getEmployeeType()));
        dto.setDeletedAt(entity.getDeletedAt());
        return dto;
    }


    public Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;
        Employee entity = new Employee();
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setEmployeeType(dto.getEmployeeType() != null ? EmployeeType.fromValue(dto.getEmployeeType()) : null);
        // Note: You must set the account relationship after saving the account
        return entity;
    }

    public List<EmployeeDto> toDtoList(List<Employee> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Employee> toEntityList(List<EmployeeDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    private String getEmployeeTypeName(EmployeeType employeeType) {
        if (employeeType == null) {
            return "Employee";
        }
        return switch (employeeType) {
            case FLIGHT_SCHEDULING -> "Flight Scheduling";
            case TICKETING -> "Ticketing";
            case SUPPORT -> "Support";
            case ACCOUNTING -> "Accounting";
            case FLIGHT_OPERATIONS -> "Flight Operations";
            case HUMAN_RESOURCES -> "Human Resources";
            case ADMINISTRATOR -> "Administrator";
        };
    }
}
