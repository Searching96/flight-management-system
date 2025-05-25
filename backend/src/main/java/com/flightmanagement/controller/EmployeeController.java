package com.flightmanagement.controller;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Integer id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByType(@PathVariable Integer type) {
        List<EmployeeDto> employees = employeeService.getEmployeesByType(type);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmail(@PathVariable String email) {
        EmployeeDto employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employee);
    }
}
