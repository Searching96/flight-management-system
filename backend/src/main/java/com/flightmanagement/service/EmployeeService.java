package com.flightmanagement.service;

import com.flightmanagement.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployeeById(Integer id);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto createEmployee(EmployeeDto dto);

    EmployeeDto updateEmployee(Integer id, EmployeeDto dto);

    void deleteEmployee(Integer id);

    List<EmployeeDto> getEmployeesByType(Integer type);

    EmployeeDto getEmployeeByEmail(String email);

    EmployeeDto updateRole(Integer id, Integer newRole);
}
