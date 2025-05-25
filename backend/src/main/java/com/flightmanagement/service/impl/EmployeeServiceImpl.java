package com.flightmanagement.service.impl;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.EmployeeMapper;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAllActive();
        return employeeMapper.toDtoList(employees);
    }
    
    @Override
    public EmployeeDto getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return employeeMapper.toDto(employee);
    }
    
    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setDeletedAt(null);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(savedEmployee);
    }
    
    @Override
    public EmployeeDto updateEmployee(Integer id, EmployeeDto employeeDto) {
        Employee existingEmployee = employeeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        existingEmployee.setEmployeeType(employeeDto.getEmployeeType());
        
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }
    
    @Override
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }
    
    @Override
    public List<EmployeeDto> getEmployeesByType(Integer employeeType) {
        List<Employee> employees = employeeRepository.findByEmployeeType(employeeType);
        return employeeMapper.toDtoList(employees);
    }
    
    @Override
    public EmployeeDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
        return employeeMapper.toDto(employee);
    }
}
