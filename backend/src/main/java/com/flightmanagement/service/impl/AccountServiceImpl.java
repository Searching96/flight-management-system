package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.AccountMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.AccountService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (dto.getAccountType() == 1) {
            Customer customer = new Customer();
            customer.setAccount(account); // Set bidirectional relationship
            account.setCustomer(customer); // Attach to account
        } else if (dto.getAccountType() == 2) {
            Employee employee = new Employee();
            employee.setAccount(account); // Set bidirectional relationship
            employee.setEmployeeType(dto.getEmployeeType());
            account.setEmployee(employee); // Attach to account
        }

        Account savedAccount = accountRepository.save(account); // Cascades save to Customer/Employee
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Integer id) {
        return accountRepository.findActiveById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    @Override
    public AccountDto getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .map(accountMapper::toDto)
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
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Integer id, AccountDto dto) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Prevent account type changes
        if (!account.getAccountType().equals(dto.getAccountType())) {
            throw new IllegalArgumentException("Account type cannot be modified");
        }

        account = accountMapper.toEntity(dto);
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
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
                .map(accountMapper::toDto)
                .toList();
    }
}
