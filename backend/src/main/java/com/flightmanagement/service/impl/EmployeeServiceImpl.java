package com.flightmanagement.service.impl;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.EmployeeType;
import com.flightmanagement.entity.Account;
import com.flightmanagement.mapper.EmployeeMapper;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.AuditLogService;
import com.flightmanagement.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final AuditLogService auditLogService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, AccountRepository accountRepository, EmployeeMapper employeeMapper, AuditLogService auditLogService) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.employeeMapper = employeeMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public EmployeeDto getCurrentEmployee(String username) {
        System.out.println("Finding current employee for username: " + username);

        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Account not found with username: " + username));

        Employee employee = employeeRepository.findByAccountId(account.getAccountId())
                .orElseThrow(() -> new RuntimeException("Employee not found for account: " + username));

        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getEmployeeById(Integer id) {
        System.out.println("Finding employee by ID: " + id);
        return employeeMapper.toDto(
                employeeRepository.findActiveById(id)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id))
        );
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        System.out.println("Getting all active employees");
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDto> getAllEmployeesPaged(Pageable pageable) {
        Page<Employee> page = employeeRepository.findByDeletedAtIsNull(pageable);
        return page.map(employeeMapper::toDto);
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Integer id, EmployeeDto updateRequest) {
        System.out.println("Updating employee ID: " + id);

        Employee existingEmployee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Store old values for audit logging
        String oldEmployeeType = existingEmployee.getEmployeeType() != null ? existingEmployee.getEmployeeType().toString() : null;
        Account account = existingEmployee.getAccount();
        String oldAccountName = account != null ? account.getAccountName() : null;
        String oldEmail = account != null ? account.getEmail() : null;
        String oldPhoneNumber = account != null ? account.getPhoneNumber() : null;

        // Update employee fields
        if (updateRequest.getEmployeeType() != null) {
            existingEmployee.setEmployeeType(EmployeeType.fromValue(updateRequest.getEmployeeType()));
        }

        // Update account fields if provided
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
        
        // Audit log for changed fields
        String newEmployeeType = updatedEmployee.getEmployeeType() != null ? updatedEmployee.getEmployeeType().toString() : null;
        if ((oldEmployeeType == null && newEmployeeType != null) || (oldEmployeeType != null && !oldEmployeeType.equals(newEmployeeType))) {
            auditLogService.saveAuditLog("Employee", id.toString(), "UPDATE", "employeeType", oldEmployeeType, newEmployeeType, "system");
        }
        
        Account updatedAccount = updatedEmployee.getAccount();
        if (updatedAccount != null) {
            String newAccountName = updatedAccount.getAccountName();
            if ((oldAccountName == null && newAccountName != null) || (oldAccountName != null && !oldAccountName.equals(newAccountName))) {
                auditLogService.saveAuditLog("Employee", id.toString(), "UPDATE", "accountName", oldAccountName, newAccountName, "system");
            }
            
            String newEmail = updatedAccount.getEmail();
            if ((oldEmail == null && newEmail != null) || (oldEmail != null && !oldEmail.equals(newEmail))) {
                auditLogService.saveAuditLog("Employee", id.toString(), "UPDATE", "email", oldEmail, newEmail, "system");
            }
            
            String newPhoneNumber = updatedAccount.getPhoneNumber();
            if ((oldPhoneNumber == null && newPhoneNumber != null) || (oldPhoneNumber != null && !oldPhoneNumber.equals(newPhoneNumber))) {
                auditLogService.saveAuditLog("Employee", id.toString(), "UPDATE", "phoneNumber", oldPhoneNumber, newPhoneNumber, "system");
            }
        }
        
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer id) {
        System.out.println("Soft deleting employee ID: " + id);

        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        
        // Capture entity info before delete
        String employeeDescription = employee.getAccount() != null ? employee.getAccount().getAccountName() : "Employee";

        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
        
        // Audit log for DELETE
        auditLogService.saveAuditLog("Employee", id.toString(), "DELETE", "employee", employeeDescription, null, "system");
    }

    @Override
    @Transactional
    public EmployeeDto activateEmployee(Integer id) {
        System.out.println("Activating employee ID: " + id);

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
        System.out.println("Deactivating employee ID: " + id);

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
        System.out.println("Checking if employee ID: " + employeeId + " is active");

        Employee employee = employeeRepository.findActiveById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return employee.getDeletedAt() == null; // If deletedAt is null, the employee is active
    }

    @Override
    @Transactional
    public EmployeeDto updateRole(Integer id, Integer newRole) {
        System.out.println("Updating role for employee ID: " + id + " to role: " + newRole);

        Employee employee = employeeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Store old value for audit logging
        String oldEmployeeType = employee.getEmployeeType() != null ? employee.getEmployeeType().toString() : null;
        
        employee.setEmployeeType(EmployeeType.fromValue(newRole));
        Employee updatedEmployee = employeeRepository.save(employee);
        
        // Audit log for changed role
        String newEmployeeType = updatedEmployee.getEmployeeType() != null ? updatedEmployee.getEmployeeType().toString() : null;
        if ((oldEmployeeType == null && newEmployeeType != null) || (oldEmployeeType != null && !oldEmployeeType.equals(newEmployeeType))) {
            auditLogService.saveAuditLog("Employee", id.toString(), "UPDATE", "employeeType", oldEmployeeType, newEmployeeType, "system");
        }
        
        return employeeMapper.toDto(updatedEmployee);
    }
}