package com.flightmanagement.controller;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee", description = "Operations related to employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Get current employee details")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<EmployeeDto>> getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmployeeEmail = authentication.getName(); // get email

        System.out.println("Getting current employee info for: " + currentEmployeeEmail);

        EmployeeDto employee = employeeService.getCurrentEmployee(currentEmployeeEmail);

        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Current employee retrieved successfully",
                employee,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get all employees")
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();

        ApiResponse<List<EmployeeDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employees retrieved successfully",
                employees,
                null
        );

        System.out.println("Getting all employees at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Integer id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employee retrieved successfully",
                employee,
                null
        );

        System.out.println("Getting employee by ID: " + id + " at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update employee details")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(@PathVariable Integer id,
                                                                   @RequestBody EmployeeDto updateRequest) {
        EmployeeDto employee = employeeService.updateEmployee(id, updateRequest);
        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employee updated successfully",
                employee,
                null
        );

        System.out.println("Updating employee ID: " + id + " at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Delete employee")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Employee deleted successfully",
                null,
                null
        );

        System.out.println("Deleting employee ID: " + id + " at " + LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @Operation(summary = "Activate employee")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<EmployeeDto>> activateEmployee(@PathVariable Integer id) {
        EmployeeDto employee = employeeService.activateEmployee(id);
        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employee activated successfully",
                employee,
                null
        );

        System.out.println("Activating employee ID: " + id + " at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Deactivate employee")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<EmployeeDto>> deactivateEmployee(@PathVariable Integer id) {
        EmployeeDto employee = employeeService.deactivateEmployee(id);
        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employee deactivated successfully",
                employee,
                null
        );

        System.out.println("Deactivating employee ID: " + id + " at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update employee role")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('EMPLOYEE_ADMINISTRATOR') or hasRole('EMPLOYEE_HUMAN_RESOURCES')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateRole(@PathVariable Integer id, @RequestParam Integer newRole) {
        EmployeeDto employee = employeeService.updateRole(id, newRole);
        ApiResponse<EmployeeDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Employee role updated successfully",
                employee,
                null
        );

        System.out.println("Updating role for employee ID: " + id + " to role: " + newRole + " at " + LocalDateTime.now());
        return ResponseEntity.ok(apiResponse);
    }
}