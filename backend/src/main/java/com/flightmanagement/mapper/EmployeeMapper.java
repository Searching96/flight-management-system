package com.flightmanagement.mapper;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
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
        dto.setEmployeeType(entity.getEmployeeType());
        dto.setEmployeeTypeName(getEmployeeTypeName(entity.getEmployeeType()));
        dto.setDeletedAt(entity.getDeletedAt());
        return dto;
    }


    public Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;
        Employee entity = new Employee();
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setEmployeeType(dto.getEmployeeType());
        // Note: You must set the account relationship after saving the account
        return entity;
    }

    public List<EmployeeDto> toDtoList(List<Employee> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Employee> toEntityList(List<EmployeeDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    private String getEmployeeTypeName(Integer employeeType) {
        return switch (employeeType) {
            case 1 -> "Reception";
            case 2 -> "Ticketing";
            case 3 -> "Support";
            case 4 -> "Accounting";
            case 5 -> "Admin";
            default -> "Employee";
        };
    }
}
