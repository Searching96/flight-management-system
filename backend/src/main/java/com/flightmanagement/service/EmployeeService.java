package com.flightmanagement.service;

import com.flightmanagement.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    
    List<EmployeeDto> getAllEmployees();
    
    EmployeeDto getEmployeeById(Integer id);
    
    EmployeeDto createEmployee(EmployeeDto employeeDto);
    
    EmployeeDto updateEmployee(Integer id, EmployeeDto employeeDto);
    
    void deleteEmployee(Integer id);
    
    List<EmployeeDto> getEmployeesByType(Integer employeeType);
    
    EmployeeDto getEmployeeByEmail(String email);
}
