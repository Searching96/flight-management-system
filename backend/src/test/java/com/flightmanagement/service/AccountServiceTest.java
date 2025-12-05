package com.flightmanagement.service;

import com.flightmanagement.dto.AccountDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.enums.EmployeeType;
import com.flightmanagement.mapper.AccountMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.service.impl.AccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AccountService - Core function for Account Management
 * 
 * Available Tags:
 * - createAccount: Tests for account creation and registration
 * - getAccountById: Tests for retrieving account by ID
 * - getAccountByEmail: Tests for retrieving account by email
 * - getAccountByName: Tests for retrieving account by name
 * - getAllAccounts: Tests for retrieving all accounts
 * - updateAccount: Tests for account updates
 * - deleteAccount: Tests for account deletion (soft delete)
 * - existsByEmail: Tests for email existence checking
 * - getAccountsByType: Tests for type-based account search
 * - verifyCurrentPassword: Tests for password verification
 * - resetPassword: Tests for password reset functionality
 */
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private AccountDto testAccountDto;
    private RegisterDto testRegisterDto;
    private Customer testCustomer;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        // Setup test account entity
        testAccount = new Account();
        testAccount.setAccountId(1);
        testAccount.setAccountName("testuser");
        testAccount.setEmail("test@email.com");
        testAccount.setPassword("encodedPassword");
        testAccount.setPhoneNumber("0123456789");
        testAccount.setCitizenId("123456789");
        testAccount.setAccountType(AccountType.CUSTOMER);
        testAccount.setDeletedAt(null);

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setAccount(testAccount);
        testAccount.setCustomer(testCustomer);

        // Setup test employee
        testEmployee = new Employee();
        testEmployee.setEmployeeId(1);
        testEmployee.setEmployeeType(EmployeeType.FLIGHT_SCHEDULING);
        testEmployee.setAccount(testAccount);

        // Setup test account DTO
        testAccountDto = new AccountDto();
        testAccountDto.setAccountId(1);
        testAccountDto.setAccountName("testuser");
        testAccountDto.setEmail("test@email.com");
        testAccountDto.setPhoneNumber("0123456789");
        testAccountDto.setCitizenId("123456789");
        testAccountDto.setAccountType(AccountType.CUSTOMER.getValue());

        // Setup test register DTO
        testRegisterDto = new RegisterDto();
        testRegisterDto.setAccountName("newuser");
        testRegisterDto.setEmail("newuser@email.com");
        testRegisterDto.setPassword("password123");
        testRegisterDto.setPhoneNumber("0987654321");
        testRegisterDto.setCitizenId("987654321");
        testRegisterDto.setAccountType(AccountType.CUSTOMER.getValue());
    }

    // ================ CREATE ACCOUNT TESTS ================

    @Test
    @Tag("createAccount")
    void testCreateAccount_CustomerSuccess_ReturnsAccountDto() {
        // Given
        testRegisterDto.setAccountType(AccountType.CUSTOMER.getValue()); // Customer
        Account mappedAccount = new Account();
        mappedAccount.setAccountName("newuser");
        mappedAccount.setEmail("newuser@email.com");
        mappedAccount.setAccountType(AccountType.CUSTOMER);

        Account savedAccount = new Account();
        savedAccount.setAccountId(2);
        savedAccount.setAccountName("newuser");
        savedAccount.setEmail("newuser@email.com");
        savedAccount.setPassword("encodedPassword123");
        savedAccount.setAccountType(AccountType.CUSTOMER);
        savedAccount.setDeletedAt(null);

        AccountDto resultDto = new AccountDto();
        resultDto.setAccountId(2);
        resultDto.setAccountName("newuser");
        resultDto.setEmail("newuser@email.com");
        resultDto.setAccountType(AccountType.CUSTOMER.getValue());

        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
        when(accountMapper.toEntity(testRegisterDto)).thenReturn(mappedAccount);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toDto(savedAccount, null)).thenReturn(resultDto);

        // When
        AccountDto result = accountService.createAccount(testRegisterDto);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getAccountId());
        assertEquals("newuser", result.getAccountName());
        assertEquals("newuser@email.com", result.getEmail());
        assertEquals(1, result.getAccountType());
        verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
        verify(passwordEncoder).encode("password123");
        verify(accountRepository).save(argThat(account -> 
            account.getCustomer() != null && account.getDeletedAt() == null));
    }

    @Test
    @Tag("createAccount")
    void testCreateAccount_EmployeeSuccess_ReturnsAccountDto() {
        // Given
        testRegisterDto.setAccountType(AccountType.EMPLOYEE.getValue()); // Employee
        testRegisterDto.setEmployeeType(EmployeeType.FLIGHT_SCHEDULING);
        
        Account mappedAccount = new Account();
        mappedAccount.setAccountType(AccountType.EMPLOYEE);

        Account savedAccount = new Account();
        savedAccount.setAccountId(3);
        savedAccount.setAccountType(AccountType.EMPLOYEE);

        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
        when(accountMapper.toEntity(testRegisterDto)).thenReturn(mappedAccount);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toDto(eq(savedAccount), any(Employee.class))).thenReturn(testAccountDto);

        // When
        AccountDto result = accountService.createAccount(testRegisterDto);

        // Then
        assertNotNull(result);
        verify(accountRepository).save(argThat(account -> 
            account.getEmployee() != null && 
            account.getEmployee().getEmployeeType().equals(EmployeeType.FLIGHT_SCHEDULING)));
    }

    @Test
    @Tag("createAccount")
    void testCreateAccount_EmailExists_ThrowsDataIntegrityViolationException() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(true);

        // When & Then
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, 
            () -> accountService.createAccount(testRegisterDto));
        assertEquals("Email already exists", exception.getMessage());
        verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
        verify(accountMapper, never()).toEntity(any(AccountDto.class));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("createAccount")
    void testCreateAccount_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
        when(accountMapper.toEntity(testRegisterDto)).thenReturn(testAccount);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(accountRepository.save(any(Account.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.createAccount(testRegisterDto));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @Tag("createAccount")
    void testCreateAccount_MapperException_PropagatesException() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
        when(accountMapper.toEntity(testRegisterDto)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.createAccount(testRegisterDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(accountMapper).toEntity(testRegisterDto);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("createAccount")
    void testCreateAccount_InvalidAccountType_NoSpecialEntity() {
        // Given
        testRegisterDto.setAccountType(999); // Invalid type
        Account mappedAccount = new Account();
        // Keep 999 as invalid value for testing error handling
        // mappedAccount.setAccountType(999);

        when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
        when(accountMapper.toEntity(testRegisterDto)).thenReturn(mappedAccount);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(accountRepository.save(any(Account.class))).thenReturn(mappedAccount);
        when(accountMapper.toDto(mappedAccount, null)).thenReturn(testAccountDto);

        // When
        AccountDto result = accountService.createAccount(testRegisterDto);

        // Then
        assertNotNull(result);
        verify(accountRepository).save(argThat(account -> 
            account.getCustomer() == null && account.getEmployee() == null));
    }

    // ================ GET ACCOUNT BY ID TESTS ================

    @Test
    @Tag("getAccountById")
    void testGetAccountById_Success_ReturnsAccountDto() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDto(testAccount, null)).thenReturn(testAccountDto);

        // When
        AccountDto result = accountService.getAccountById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAccountId());
        assertEquals("testuser", result.getAccountName());
        assertEquals("test@email.com", result.getEmail());
        verify(accountRepository).findActiveById(1);
        verify(accountMapper).toDto(testAccount, null);
    }

    @Test
    @Tag("getAccountById")
    void testGetAccountById_NotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountById(999));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(999);
        verify(accountMapper, never()).toDto(any(Account.class), any());
    }

    @Test
    @Tag("getAccountById")
    void testGetAccountById_NullId_HandledByRepository() {
        // Given
        when(accountRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountById(null));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(null);
    }

    @Test
    @Tag("getAccountById")
    void testGetAccountById_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountById(1));
        assertEquals("Database connection failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
    }

    @Test
    @Tag("getAccountById")
    void testGetAccountById_MapperException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDto(testAccount, null)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountById(1));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(accountMapper).toDto(testAccount, null);
    }

    // ================ GET ACCOUNT BY EMAIL TESTS ================

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_Success_ReturnsAccountDto() {
        // Given
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDto(testAccount, null)).thenReturn(testAccountDto);

        // When
        AccountDto result = accountService.getAccountByEmail("test@email.com");

        // Then
        assertNotNull(result);
        assertEquals("test@email.com", result.getEmail());
        assertEquals("testuser", result.getAccountName());
        verify(accountRepository).findByEmail("test@email.com");
        verify(accountMapper).toDto(testAccount, null);
    }

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_NotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByEmail("nonexistent@email.com"));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_NullEmail_HandledByRepository() {
        // Given
        when(accountRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByEmail(null));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByEmail(null);
    }

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_EmptyEmail_HandledByRepository() {
        // Given
        when(accountRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByEmail(""));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByEmail("");
    }

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findByEmail("test@email.com")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountByEmail("test@email.com"));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findByEmail("test@email.com");
    }

    @Test
    @Tag("getAccountByEmail")
    void testGetAccountByEmail_MapperException_PropagatesException() {
        // Given
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDto(testAccount, null)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountByEmail("test@email.com"));
        assertEquals("Mapping error", exception.getMessage());
        verify(accountRepository).findByEmail("test@email.com");
        verify(accountMapper).toDto(testAccount, null);
    }

    // ================ GET ACCOUNT BY NAME TESTS ================

    @Test
    @Tag("getAccountByName")
    void testGetAccountByName_Success_ReturnsAccount() {
        // Given
        when(accountRepository.findByAccountName("testuser")).thenReturn(Optional.of(testAccount));

        // When
        Account result = accountService.getAccountByName("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getAccountName());
        assertEquals("test@email.com", result.getEmail());
        verify(accountRepository).findByAccountName("testuser");
    }

    @Test
    @Tag("getAccountByName")
    void testGetAccountByName_NotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findByAccountName("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByName("nonexistent"));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByAccountName("nonexistent");
    }

    @Test
    @Tag("getAccountByName")
    void testGetAccountByName_NullName_HandledByRepository() {
        // Given
        when(accountRepository.findByAccountName(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByName(null));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByAccountName(null);
    }

    @Test
    @Tag("getAccountByName")
    void testGetAccountByName_EmptyString_HandledByRepository() {
        // Given
        when(accountRepository.findByAccountName("")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.getAccountByName(""));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findByAccountName("");
    }

    @Test
    @Tag("getAccountByName")
    void testGetAccountByName_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findByAccountName("testuser")).thenThrow(new RuntimeException("Query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountByName("testuser"));
        assertEquals("Query failed", exception.getMessage());
        verify(accountRepository).findByAccountName("testuser");
    }

    // ================ GET ALL ACCOUNTS TESTS ================

    @Test
    @Tag("getAllAccounts")
    void testGetAllAccounts_Success_ReturnsAccountList() {
        // Given
        Account account2 = new Account();
        account2.setAccountId(2);
        account2.setAccountName("user2");
        account2.setEmail("user2@email.com");

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setAccountId(2);
        accountDto2.setAccountName("user2");
        accountDto2.setEmail("user2@email.com");

        List<Account> accounts = Arrays.asList(testAccount, account2);

        when(accountRepository.findAllActive()).thenReturn(accounts);
        when(accountMapper.toDto(testAccount, null)).thenReturn(testAccountDto);
        when(accountMapper.toDto(account2, null)).thenReturn(accountDto2);

        // When
        List<AccountDto> result = accountService.getAllAccounts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getAccountName());
        assertEquals("user2", result.get(1).getAccountName());
        verify(accountRepository).findAllActive();
        verify(accountMapper, times(2)).toDto(any(Account.class), eq(null));
    }

    @Test
    @Tag("getAllAccounts")
    void testGetAllAccounts_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(accountRepository.findAllActive()).thenReturn(Collections.emptyList());

        // When
        List<AccountDto> result = accountService.getAllAccounts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository).findAllActive();
        verify(accountMapper, never()).toDto(any(Account.class), any());
    }

    @Test
    @Tag("getAllAccounts")
    void testGetAllAccounts_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findAllActive()).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAllAccounts());
        assertEquals("Database connection lost", exception.getMessage());
        verify(accountRepository).findAllActive();
    }

    @Test
    @Tag("getAllAccounts")
    void testGetAllAccounts_MapperException_PropagatesException() {
        // Given
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findAllActive()).thenReturn(accounts);
        when(accountMapper.toDto(testAccount, null)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAllAccounts());
        assertEquals("Mapping failed", exception.getMessage());
        verify(accountRepository).findAllActive();
        verify(accountMapper).toDto(testAccount, null);
    }

    @Test
    @Tag("getAllAccounts")
    void testGetAllAccounts_LargeDataset_HandlesEfficiently() {
        // Given
        List<Account> largeAccountList = Arrays.asList(testAccount, testAccount, testAccount, testAccount, testAccount);
        when(accountRepository.findAllActive()).thenReturn(largeAccountList);
        when(accountMapper.toDto(any(Account.class), eq(null))).thenReturn(testAccountDto);

        // When
        List<AccountDto> result = accountService.getAllAccounts();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(accountRepository).findAllActive();
        verify(accountMapper, times(5)).toDto(any(Account.class), eq(null));
    }

    // ================ UPDATE ACCOUNT TESTS ================

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_Success_ReturnsUpdatedAccountDto() {
        // Given
        AccountDto updateDto = new AccountDto();
        updateDto.setAccountName("updateduser");
        updateDto.setEmail("updated@email.com");
        updateDto.setPhoneNumber("0999999999");
        updateDto.setCitizenId("999999999");

        Account updatedAccount = new Account();
        updatedAccount.setAccountId(1);
        updatedAccount.setAccountName("updateduser");
        updatedAccount.setEmail("updated@email.com");
        updatedAccount.setPhoneNumber("0999999999");
        updatedAccount.setCitizenId("999999999");

        AccountDto resultDto = new AccountDto();
        resultDto.setAccountId(1);
        resultDto.setAccountName("updateduser");
        resultDto.setEmail("updated@email.com");
        resultDto.setPhoneNumber("0999999999");
        resultDto.setCitizenId("999999999");

        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(updatedAccount);
        when(accountMapper.toDto(updatedAccount, null)).thenReturn(resultDto);

        // When
        AccountDto result = accountService.updateAccount(1, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("updateduser", result.getAccountName());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("0999999999", result.getPhoneNumber());
        assertEquals("999999999", result.getCitizenId());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
        verify(accountMapper).toDto(updatedAccount, null);
        
        // Verify original entity was updated
        assertEquals("updateduser", testAccount.getAccountName());
        assertEquals("updated@email.com", testAccount.getEmail());
        assertEquals("0999999999", testAccount.getPhoneNumber());
        assertEquals("999999999", testAccount.getCitizenId());
    }

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_PartialUpdate_UpdatesOnlyProvidedFields() {
        // Given
        AccountDto partialUpdateDto = new AccountDto();
        partialUpdateDto.setAccountName("partialupdate");
        // Other fields are null

        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(accountMapper.toDto(testAccount, null)).thenReturn(testAccountDto);

        String originalEmail = testAccount.getEmail();
        String originalPhone = testAccount.getPhoneNumber();

        // When
        AccountDto result = accountService.updateAccount(1, partialUpdateDto);

        // Then
        assertNotNull(result);
        assertEquals("partialupdate", testAccount.getAccountName());
        assertEquals(originalEmail, testAccount.getEmail()); // Should remain unchanged
        assertEquals(originalPhone, testAccount.getPhoneNumber()); // Should remain unchanged
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_NotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.updateAccount(999, testAccountDto));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(999);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_NullFields_DoesNotUpdateNullFields() {
        // Given
        AccountDto nullFieldsDto = new AccountDto();
        // All fields are null

        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(accountMapper.toDto(testAccount, null)).thenReturn(testAccountDto);

        String originalName = testAccount.getAccountName();
        String originalEmail = testAccount.getEmail();

        // When
        AccountDto result = accountService.updateAccount(1, nullFieldsDto);

        // Then
        assertNotNull(result);
        assertEquals(originalName, testAccount.getAccountName());
        assertEquals(originalEmail, testAccount.getEmail());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenThrow(new RuntimeException("Update failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.updateAccount(1, testAccountDto));
        assertEquals("Update failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("updateAccount")
    void testUpdateAccount_MapperException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(accountMapper.toDto(testAccount, null)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.updateAccount(1, testAccountDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
        verify(accountMapper).toDto(testAccount, null);
    }

    // ================ DELETE ACCOUNT TESTS ================

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_Success_SetsDeletedAt() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        // When
        accountService.deleteAccount(1);

        // Then
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(argThat(account -> account.getDeletedAt() != null));
        assertNotNull(testAccount.getDeletedAt());
        assertTrue(testAccount.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_NotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.deleteAccount(999));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(999);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_AlreadyDeleted_UpdatesDeletedAt() {
        // Given
        testAccount.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        // When
        accountService.deleteAccount(1);

        // Then
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
        assertTrue(testAccount.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_RepositoryFindException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.deleteAccount(1));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_RepositorySaveException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenThrow(new RuntimeException("Save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.deleteAccount(1));
        assertEquals("Save failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("deleteAccount")
    void testDeleteAccount_NullId_HandledByRepository() {
        // Given
        when(accountRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.deleteAccount(null));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(null);
    }

    // ================ EXISTS BY EMAIL TESTS ================

    @Test
    @Tag("existsByEmail")
    void testExistsByEmail_EmailExists_ReturnsTrue() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("test@email.com")).thenReturn(true);

        // When
        boolean result = accountService.existsByEmail("test@email.com");

        // Then
        assertTrue(result);
        verify(accountRepository).existsByEmailAndNotDeleted("test@email.com");
    }

    @Test
    @Tag("existsByEmail")
    void testExistsByEmail_EmailNotExists_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("nonexistent@email.com")).thenReturn(false);

        // When
        boolean result = accountService.existsByEmail("nonexistent@email.com");

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted("nonexistent@email.com");
    }

    @Test
    @Tag("existsByEmail")
    void testExistsByEmail_NullEmail_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted(null)).thenReturn(false);

        // When
        boolean result = accountService.existsByEmail(null);

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted(null);
    }

    @Test
    @Tag("existsByEmail")
    void testExistsByEmail_EmptyEmail_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("")).thenReturn(false);

        // When
        boolean result = accountService.existsByEmail("");

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted("");
    }

    @Test
    @Tag("existsByEmail")
    void testExistsByEmail_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("test@email.com"))
            .thenThrow(new RuntimeException("Query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.existsByEmail("test@email.com"));
        assertEquals("Query failed", exception.getMessage());
        verify(accountRepository).existsByEmailAndNotDeleted("test@email.com");
    }

    // ================ GET ACCOUNTS BY TYPE TESTS ================

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_Success_ReturnsFilteredAccounts() {
        // Given
        Account customer1 = new Account();
        customer1.setAccountId(1);
        customer1.setAccountType(AccountType.CUSTOMER);
        customer1.setDeletedAt(null);

        Account customer2 = new Account();
        customer2.setAccountId(2);
        customer2.setAccountType(AccountType.CUSTOMER);
        customer2.setDeletedAt(null);

        List<Account> customers = Arrays.asList(customer1, customer2);

        AccountDto customerDto1 = new AccountDto();
        customerDto1.setAccountId(1);
        customerDto1.setAccountType(AccountType.CUSTOMER.getValue());

        AccountDto customerDto2 = new AccountDto();
        customerDto2.setAccountId(2);
        customerDto2.setAccountType(AccountType.CUSTOMER.getValue());

        when(accountRepository.findByAccountType(1)).thenReturn(customers);
        when(accountMapper.toDto(customer1, null)).thenReturn(customerDto1);
        when(accountMapper.toDto(customer2, null)).thenReturn(customerDto2);

        // When
        List<AccountDto> result = accountService.getAccountsByType(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getAccountType());
        assertEquals(1, result.get(1).getAccountType());
        verify(accountRepository).findByAccountType(1);
        verify(accountMapper, times(2)).toDto(any(Account.class), eq(null));
    }

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_WithDeletedAccounts_FiltersDeletedAccounts() {
        // Given
        Account activeAccount = new Account();
        activeAccount.setAccountId(1);
        activeAccount.setAccountType(AccountType.CUSTOMER);
        activeAccount.setDeletedAt(null);

        Account deletedAccount = new Account();
        deletedAccount.setAccountId(2);
        deletedAccount.setAccountType(AccountType.CUSTOMER);
        deletedAccount.setDeletedAt(LocalDateTime.now());

        List<Account> accounts = Arrays.asList(activeAccount, deletedAccount);

        AccountDto activeDto = new AccountDto();
        activeDto.setAccountId(1);
        activeDto.setAccountType(AccountType.CUSTOMER.getValue());

        when(accountRepository.findByAccountType(1)).thenReturn(accounts);
        when(accountMapper.toDto(activeAccount, null)).thenReturn(activeDto);

        // When
        List<AccountDto> result = accountService.getAccountsByType(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only active account
        assertEquals(1, result.get(0).getAccountId());
        verify(accountRepository).findByAccountType(1);
        verify(accountMapper, times(1)).toDto(activeAccount, null);
        verify(accountMapper, never()).toDto(eq(deletedAccount), any(Employee.class));
    }

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_NoAccountsOfType_ReturnsEmptyList() {
        // Given
        when(accountRepository.findByAccountType(999)).thenReturn(Collections.emptyList());

        // When
        List<AccountDto> result = accountService.getAccountsByType(999);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository).findByAccountType(999);
        verify(accountMapper, never()).toDto(any(Account.class), any());
    }

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_NullType_HandledByRepository() {
        // Given
        when(accountRepository.findByAccountType(null)).thenReturn(Collections.emptyList());

        // When
        List<AccountDto> result = accountService.getAccountsByType(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository).findByAccountType(null);
    }

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findByAccountType(1)).thenThrow(new RuntimeException("Type query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountsByType(1));
        assertEquals("Type query failed", exception.getMessage());
        verify(accountRepository).findByAccountType(1);
    }

    @Test
    @Tag("getAccountsByType")
    void testGetAccountsByType_MapperException_PropagatesException() {
        // Given
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByAccountType(1)).thenReturn(accounts);
        when(accountMapper.toDto(testAccount, null)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.getAccountsByType(1));
        assertEquals("Mapping error", exception.getMessage());
        verify(accountRepository).findByAccountType(1);
        verify(accountMapper).toDto(testAccount, null);
    }

    // ================ VERIFY CURRENT PASSWORD TESTS ================

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_CorrectPassword_ReturnsTrue() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("correctPassword", testAccount.getPassword())).thenReturn(true);

        // When
        boolean result = accountService.verifyCurrentPassword(1, "correctPassword");

        // Then
        assertTrue(result);
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches("correctPassword", testAccount.getPassword());
    }

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_IncorrectPassword_ReturnsFalse() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPassword", testAccount.getPassword())).thenReturn(false);

        // When
        boolean result = accountService.verifyCurrentPassword(1, "wrongPassword");

        // Then
        assertFalse(result);
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches("wrongPassword", testAccount.getPassword());
    }

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_AccountNotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.verifyCurrentPassword(999, "password"));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(999);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_NullPassword_HandledByEncoder() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(null, testAccount.getPassword())).thenReturn(false);

        // When
        boolean result = accountService.verifyCurrentPassword(1, null);

        // Then
        assertFalse(result);
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches(null, testAccount.getPassword());
    }

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.verifyCurrentPassword(1, "password"));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findActiveById(1);
    }

    @Test
    @Tag("verifyCurrentPassword")
    void testVerifyCurrentPassword_PasswordEncoderException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("password", testAccount.getPassword()))
            .thenThrow(new RuntimeException("Encoding error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.verifyCurrentPassword(1, "password"));
        assertEquals("Encoding error", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches("password", testAccount.getPassword());
    }

    // ================ RESET PASSWORD TESTS ================

    @Test
    @Tag("resetPassword")
    void testResetPassword_Success_UpdatesPassword() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("currentPassword", testAccount.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        // When
        accountService.resetPassword(1, "currentPassword", "newPassword");

        // Then
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(accountRepository).save(testAccount);
        assertEquals("encodedNewPassword", testAccount.getPassword());
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_IncorrectCurrentPassword_ThrowsIllegalArgumentException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPassword", testAccount.getPassword())).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> accountService.resetPassword(1, "wrongPassword", "newPassword"));
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(accountRepository).findActiveById(1);
        verify(passwordEncoder).matches("wrongPassword", testAccount.getPassword());
        verify(passwordEncoder, never()).encode("newPassword");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_AccountNotFound_ThrowsEntityNotFoundException() {
        // Given
        when(accountRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> accountService.resetPassword(999, "currentPassword", "newPassword"));
        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findActiveById(999);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_RepositoryFindException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.resetPassword(1, "currentPassword", "newPassword"));
        assertEquals("Database connection failed", exception.getMessage());
        verify(accountRepository).findActiveById(1);
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_PasswordEncodingException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("currentPassword", testAccount.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenThrow(new RuntimeException("Encoding failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.resetPassword(1, "currentPassword", "newPassword"));
        assertEquals("Encoding failed", exception.getMessage());
        verify(passwordEncoder).encode("newPassword");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_RepositorySaveException_PropagatesException() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("currentPassword", testAccount.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(accountRepository.save(testAccount)).thenThrow(new RuntimeException("Save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> accountService.resetPassword(1, "currentPassword", "newPassword"));
        assertEquals("Save failed", exception.getMessage());
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("resetPassword")
    void testResetPassword_NullPasswords_HandledByEncoder() {
        // Given
        when(accountRepository.findActiveById(1)).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(null, testAccount.getPassword())).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> accountService.resetPassword(1, null, "newPassword"));
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(passwordEncoder).matches(null, testAccount.getPassword());
    }
}