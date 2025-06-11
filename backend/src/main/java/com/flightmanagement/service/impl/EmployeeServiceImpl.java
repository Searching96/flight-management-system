package com.flightmanagement.service.impl;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.entity.Account;
import com.flightmanagement.mapper.EmployeeMapper;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, AccountRepository accountRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDto getCurrentEmployee(String username) {
        System.out.println("Finding current employee for username: " + username + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Account account = accountRepository.findByAccountName(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));

        Employee employee = employeeRepository.findByAccountId(account.getAccountId())
                .orElseThrow(() -> new RuntimeException("Employee not found for account: " + username));

        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getEmployeeById(Integer id) {
        System.out.println("Finding employee by ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return employeeMapper.toDto(
                employeeRepository.findActiveById(id)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id))
        );
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        System.out.println("Getting all active employees at 2025-06-11 07:43:20 UTC by thinh0704hcm");
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EmployeeDto updateEmployee(Integer id, EmployeeDto updateRequest) {
        System.out.println("Updating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee existingEmployee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update employee fields
        if (updateRequest.getEmployeeType() != null) {
            existingEmployee.setEmployeeType(updateRequest.getEmployeeType());
        }

        // Update account fields if provided
        Account account = existingEmployee.getAccount();
        if (account != null) {
            if (updateRequest.getAccountName() != null) {
                account.setAccountName(updateRequest.getAccountName());
            }
            if (updateRequest.getEmail() != null) {
                account.setEmail(updateRequest.getEmail());
            }
            if (updateRequest.getPhoneNumber() != null) {
                account.setPhoneNumber(updateRequest.getPhoneNumber());
            }
            accountRepository.save(account);
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer id) {
        System.out.println("Soft deleting employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public EmployeeDto activateEmployee(Integer id) {
        System.out.println("Activating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setDeletedAt(null); // Remove deletion timestamp to activate
        Employee activatedEmployee = employeeRepository.save(employee);

        Account account = employee.getAccount();
        account.setDeletedAt(null); // Remove deletion timestamp from account
        accountRepository.save(account);

        return employeeMapper.toDto(activatedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDto deactivateEmployee(Integer id) {
        System.out.println("Deactivating employee ID: " + id + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setDeletedAt(LocalDateTime.now());
        Employee deactivatedEmployee = employeeRepository.save(employee);

        Account account = employee.getAccount();
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);

        return employeeMapper.toDto(deactivatedEmployee);
    }

    @Override
    @Transactional
    public boolean isEmployeeActive(Integer employeeId) {
        System.out.println("Checking if employee ID: " + employeeId + " is active at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee employee = employeeRepository.findActiveById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return employee.getDeletedAt() == null; // If deletedAt is null, the employee is active
    }

    @Override
    @Transactional
    public EmployeeDto updateRole(Integer id, Integer newRole) {
        System.out.println("Updating role for employee ID: " + id + " to role: " + newRole + " at 2025-06-11 07:43:20 UTC by thinh0704hcm");

        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setEmployeeType(newRole);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(updatedEmployee);
    }
}