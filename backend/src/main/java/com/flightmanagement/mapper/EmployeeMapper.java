package com.flightmanagement.mapper;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper implements BaseMapper<Employee, EmployeeDto> {
    
    @Override
    public EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;
        
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setEmployeeType(entity.getEmployeeType());
        dto.setEmployeeTypeName(getEmployeeTypeName(entity.getEmployeeType()));
        
        if (entity.getAccount() != null) {
            dto.setAccountName(entity.getAccount().getAccountName());
            dto.setEmail(entity.getAccount().getEmail());
            dto.setPhoneNumber(entity.getAccount().getPhoneNumber());
        }
        
        return dto;
    }
    
    @Override
    public Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;
        
        Employee entity = new Employee();
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setEmployeeType(dto.getEmployeeType());
        return entity;
    }
    
    @Override
    public List<EmployeeDto> toDtoList(List<Employee> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    public List<Employee> toEntityList(List<EmployeeDto> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    private String getEmployeeTypeName(Integer employeeType) {
        return switch (employeeType) {
            case 1 -> "Flight Schedule Reception";
            case 2 -> "Ticket Sales/Booking";
            case 3 -> "Customer Service";
            case 4 -> "Accounting";
            case 5 -> "System Administrator";
            default -> "Unknown";
        };
    }
}