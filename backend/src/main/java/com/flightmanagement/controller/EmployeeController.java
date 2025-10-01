package com.flightmanagement.controller;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Get current logged-in employee information
     */
    @GetMapping("/current")
    public ResponseEntity<EmployeeDto> getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        System.out.println("Getting current employee info for: " + currentUsername + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        EmployeeDto employee = employeeService.getCurrentEmployee(currentUsername);
        return ResponseEntity.ok(employee);
    }

    /**
     * Get all employees (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        System.out.println("Getting all employees at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * Get employee by ID (Admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Integer id) {
        System.out.println("Getting employee by ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * Update employee (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeDto updateRequest) {
        System.out.println("Updating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
    }

    /**
     * Delete employee (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        System.out.println("Deleting employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Activate employee (Admin only)
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDto> activateEmployee(@PathVariable Integer id) {
        System.out.println("Activating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.activateEmployee(id));
    }

    /**
     * Deactivate employee (Admin only)
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDto> deactivateEmployee(@PathVariable Integer id) {
        System.out.println("Deactivating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.deactivateEmployee(id));
    }

    /**
     * Update employee role (Admin only)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<EmployeeDto> updateRole(@PathVariable Integer id, @RequestParam Integer newRole) {
        System.out.println("Updating role for employee ID: " + id + " to role: " + newRole + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return ResponseEntity.ok(employeeService.updateRole(id, newRole));
    }
}