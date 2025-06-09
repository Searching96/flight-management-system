package com.flightmanagement.service.impl;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.EmployeeMapper;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDto getEmployeeById(Integer id) {
        return employeeMapper.toDto(
                employeeRepository.findActiveById(id)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id))
        );
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAllActive()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        Employee employee = employeeMapper.toEntity(dto);
        employee.setDeletedAt(null);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Integer id, EmployeeDto dto) {
        Employee existingEmployee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update only allowed fields (e.g., employeeType, phone, etc.)
        existingEmployee.setEmployeeType(dto.getEmployeeType());
        // Add more fields as needed

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeDto> getEmployeesByType(Integer type) {
        return employeeRepository.findByEmployeeType(type)
                .stream()
                .filter(emp -> emp.getDeletedAt() == null)
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto getEmployeeByEmail(String email) {
        return employeeMapper.toDto(
                employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email))
        );
    }

    @Override
    public EmployeeDto updateRole(Integer id, Integer newRole) {
        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Assuming role is represented by employeeType
        employee.setEmployeeType(newRole);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }
}
