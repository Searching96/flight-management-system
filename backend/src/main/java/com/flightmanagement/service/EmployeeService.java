package com.flightmanagement.service;

import com.flightmanagement.dto.EmployeeDto;

import java.util.List;

/**
 * Employee Service Interface
 * Provides comprehensive employee management functionality
 *
 * Last updated: 2025-06-11 07:46:23 UTC by thinh0704hcm
 */
public interface EmployeeService {

    // ===============================
    // BASIC CRUD OPERATIONS
    // ===============================

    /**
     * Get all active employees
     * @return List of all active employee DTOs
     */
    List<EmployeeDto> getAllEmployees();

    /**
     * Get employee by ID
     * @param employeeId Employee ID
     * @return Employee DTO if found
     * @throws RuntimeException if employee not found
     */
    EmployeeDto getEmployeeById(Integer employeeId);

    /**
     * Get current logged-in employee
     * @return Current employee DTO
     * @throws RuntimeException if no employee is logged in
     */
    EmployeeDto getCurrentEmployee(String accountName);

    /**
     * Update employee information
     * @param employeeId Employee ID to update
     * @param updateRequest Update data
     * @return Updated employee DTO
     * @throws RuntimeException if employee not found or update fails
     */
    EmployeeDto updateEmployee(Integer employeeId, EmployeeDto updateRequest);

    /**
     * Delete employee (soft delete)
     * @param employeeId Employee ID to delete
     * @throws RuntimeException if employee not found
     */
    void deleteEmployee(Integer employeeId);

    /**
     * Update an employee's role
     * @param id
     * @param newRole
     * @return
     */
    EmployeeDto updateRole(Integer id, Integer newRole);

    // ===============================
    // EMPLOYEE STATUS MANAGEMENT
    // ===============================

    /**
     * Activate employee account
     * @param employeeId Employee ID to activate
     * @return Updated employee DTO
     * @throws RuntimeException if employee not found
     */
    EmployeeDto activateEmployee(Integer employeeId);

    /**
     * Deactivate employee account
     * @param employeeId Employee ID to deactivate
     * @return Updated employee DTO
     * @throws RuntimeException if employee not found
     */
    EmployeeDto deactivateEmployee(Integer employeeId);

    /**
     * Check if employee is active
     * @param employeeId Employee ID to check
     * @return true if employee is active, false otherwise
     */
    boolean isEmployeeActive(Integer employeeId);
}