package com.flightmanagement.service;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.LoginDto;
import com.flightmanagement.dto.LoginResponseDto;
import com.flightmanagement.dto.RegisterDto;

import java.util.List;

public interface AccountService {
    
    List<AccountDto> getAllAccounts();
    
    AccountDto getAccountById(Integer id);
    
    AccountDto createAccount(RegisterDto registerDto);
    
    AccountDto updateAccount(Integer id, AccountDto accountDto);
    
    void deleteAccount(Integer id);
    
    AccountDto getAccountByEmail(String email);
    
    AccountDto getAccountByCitizenId(String citizenId);
    
    AccountDto login(LoginDto loginDto);
    
    LoginResponseDto login(String email, String password);
    
    List<AccountDto> getAccountsByType(Integer accountType);

    LoginResponseDto debugLoginByName(String accountName);
}
