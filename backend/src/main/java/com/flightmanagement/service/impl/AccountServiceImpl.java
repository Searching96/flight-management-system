package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.mapper.AccountMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.AccountService;
import com.flightmanagement.service.AuditLogService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public AccountDto createAccount(RegisterDto dto) {
        if (existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        Account account = accountMapper.toEntity(dto);
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setDeletedAt(null);

        // Create associated entity BEFORE saving
        if (dto.getAccountType() == AccountType.CUSTOMER.getValue()) {
            Customer customer = new Customer();
            customer.setAccount(account); // Set bidirectional relationship
            account.setCustomer(customer); // Attach to account
            Account savedAccount = accountRepository.save(account); // Cascades save to Customer/Employee
            return accountMapper.toDto(savedAccount, null);
        } else if (dto.getAccountType() == AccountType.EMPLOYEE.getValue()) {
            Employee employee = new Employee();
            employee.setAccount(account); // Set bidirectional relationship
            employee.setEmployeeType(dto.getEmployeeType());
            account.setEmployee(employee); // Attach to account
            Account savedAccount = accountRepository.save(account); // Cascades save to Customer/Employee
            auditLogService.saveAuditLog("Account", savedAccount.getAccountId().toString(), "CREATE", "account", null, savedAccount.getAccountName() + " (" + savedAccount.getEmail() + ")", savedAccount.getEmail());
            return accountMapper.toDto(savedAccount, employee);
        }

        Account savedAccount = accountRepository.save(account); // Cascades save to Customer/Employee
        auditLogService.saveAuditLog("Account", savedAccount.getAccountId().toString(), "CREATE", "account", null, savedAccount.getAccountName() + " (" + savedAccount.getEmail() + ")", savedAccount.getEmail());
        return accountMapper.toDto(savedAccount, null);
    }

    @Override
    public AccountDto getAccountById(Integer id) {
        return accountRepository.findActiveById(id)
                .map(account -> accountMapper.toDto(account, account.getEmployee()))
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    @Override
    public AccountDto getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .map(account -> accountMapper.toDto(account, account.getEmployee()))
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    @Override
    public Account getAccountByName(String accountName)
    {
        return accountRepository.findByAccountName(accountName)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }


    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAllActive()
                .stream()
                .map(account -> accountMapper.toDto(account, account.getEmployee()))
                .toList();
    }

    @Override
    public Page<AccountDto> getAllAccountsPaged(Pageable pageable) {
        Page<Account> page = accountRepository.findByDeletedAtIsNull(pageable);
        return page.map(account -> accountMapper.toDto(account, account.getEmployee()));
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Integer id, AccountDto dto) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Store old values and update only the allowed fields
        String oldAccountName = account.getAccountName();
        String oldEmail = account.getEmail();
        String oldPhoneNumber = account.getPhoneNumber();
        String oldCitizenId = account.getCitizenId();
        
        if (dto.getAccountName() != null && !dto.getAccountName().equals(oldAccountName)) {
            account.setAccountName(dto.getAccountName());
            auditLogService.saveAuditLog("Account", id.toString(), "UPDATE", "accountName", oldAccountName, dto.getAccountName(), dto.getEmail() != null ? dto.getEmail() : oldEmail);
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(oldEmail)) {
            account.setEmail(dto.getEmail());
            auditLogService.saveAuditLog("Account", id.toString(), "UPDATE", "email", oldEmail, dto.getEmail(), dto.getEmail());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(oldPhoneNumber)) {
            account.setPhoneNumber(dto.getPhoneNumber());
            auditLogService.saveAuditLog("Account", id.toString(), "UPDATE", "phoneNumber", oldPhoneNumber, dto.getPhoneNumber(), account.getEmail());
        }
        if (dto.getCitizenId() != null && !dto.getCitizenId().equals(oldCitizenId)) {
            account.setCitizenId(dto.getCitizenId());
            auditLogService.saveAuditLog("Account", id.toString(), "UPDATE", "citizenId", oldCitizenId, dto.getCitizenId(), account.getEmail());
        }
        // Don't update accountType, password, or other sensitive fields

        return accountMapper.toDto(accountRepository.save(account), account.getEmployee());
    }

    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
        auditLogService.saveAuditLog("Account", id.toString(), "DELETE", "account", account.getAccountName() + " (" + account.getEmail() + ")", null, "system");
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmailAndNotDeleted(email);
    }

    @Override
    public List<AccountDto> getAccountsByType(Integer accountType) {
        return accountRepository.findByAccountType(accountType)
                .stream()
                .filter(acc -> acc.getDeletedAt() == null)
                .map(account -> accountMapper.toDto(account, account.getEmployee()))
                .toList();
    }

    @Override
    public boolean verifyCurrentPassword(Integer accountId, String currentPassword) {
        Account account = accountRepository.findActiveById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        
        // Use the same password verification logic as login
        return passwordEncoder.matches(currentPassword, account.getPassword());
    }

    @Override
    @Transactional
    public void resetPassword(Integer accountId, String currentPassword, String newPassword) {
        Account account = accountRepository.findActiveById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        
        // Verify current password first (same logic as login)
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Encode and set new password
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
