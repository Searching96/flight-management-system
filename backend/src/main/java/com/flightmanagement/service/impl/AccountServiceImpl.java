package com.flightmanagement.service.impl;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.LoginDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.mapper.AccountMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accountMapper.toDtoList(accounts);
    }
    
    @Override
    public AccountDto getAccountById(Integer id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        return accountMapper.toDto(account);
    }
    
    @Override
    public AccountDto createAccount(RegisterDto registerDto) {
        // Check if email already exists
        try {
            getAccountByEmail(registerDto.getEmail());
            throw new RuntimeException("Email already exists: " + registerDto.getEmail());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                throw e;
            }
            // Email doesn't exist, proceed with creation
        }
        
        Account account = new Account();
        account.setAccountName(registerDto.getAccountName());
        account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        account.setEmail(registerDto.getEmail());
        account.setCitizenId(registerDto.getCitizenId());
        account.setPhoneNumber(registerDto.getPhoneNumber());
        account.setAccountType(registerDto.getAccountType());
        
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }
    
    @Override
    public AccountDto updateAccount(Integer id, AccountDto accountDto) {
        Account existingAccount = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        existingAccount.setAccountName(accountDto.getAccountName());
        existingAccount.setEmail(accountDto.getEmail());
        existingAccount.setCitizenId(accountDto.getCitizenId());
        existingAccount.setPhoneNumber(accountDto.getPhoneNumber());
        existingAccount.setAccountType(accountDto.getAccountType());
        
        Account updatedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(updatedAccount);
    }
    
    @Override
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        accountRepository.delete(account); // Hard delete instead of soft delete
    }
    
    @Override
    public AccountDto getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));
        return accountMapper.toDto(account);
    }
    
    @Override
    public AccountDto getAccountByCitizenId(String citizenId) {
        Account account = accountRepository.findByCitizenId(citizenId)
            .orElseThrow(() -> new RuntimeException("Account not found with citizen ID: " + citizenId));
        return accountMapper.toDto(account);
    }
    
    @Override
    public AccountDto login(LoginDto loginDto) {
        Account account = accountRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginDto.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        return accountMapper.toDto(account);
    }
    
    @Override
    public List<AccountDto> getAccountsByType(Integer accountType) {
        List<Account> accounts = accountRepository.findByAccountType(accountType);
        return accountMapper.toDtoList(accounts);
    }

    @Override
    public LoginResponseDto login(String email, String password) {
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token here if needed
        // String token = jwtTokenProvider.generateToken(account);
        
        return new LoginResponseDto(
            account.getAccountId(),
            account.getAccountName(), // Username for display
            account.getEmail(),
            account.getAccountType()
            // token
        );
    }
}
