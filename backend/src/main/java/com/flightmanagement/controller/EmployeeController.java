package com.flightmanagement.controller;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// EmployeeController.java - Admin-restricted employee management
@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('EMPLOYEE_ADMIN')")
    public ResponseEntity<EmployeeDto> updateRole(@PathVariable Integer id, @RequestParam Integer newRole) {
        return ResponseEntity.ok(employeeService.updateRole(id, newRole));
    }
}
