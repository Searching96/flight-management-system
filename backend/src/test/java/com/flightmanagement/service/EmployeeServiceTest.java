package com.flightmanagement.service;

import com.flightmanagement.dto.EmployeeDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.EmployeeMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for EmployeeService - Core function for Employee Management
 * 
 * Available Tags:
 * - getAllEmployees: Tests for retrieving all employees
 * - getEmployeeById: Tests for retrieving employee by ID
 * - getCurrentEmployee: Tests for getting current logged-in employee
 * - updateEmployee: Tests for employee updates
 * - deleteEmployee: Tests for employee deletion (soft delete)
 * - updateRole: Tests for employee role updates
 * - activateEmployee: Tests for employee activation
 * - deactivateEmployee: Tests for employee deactivation
 * - isEmployeeActive: Tests for checking employee active status
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee testEmployee;
    private EmployeeDto testEmployeeDto;
    private Account testAccount;
    private Employee testEmployee2;
    private EmployeeDto testEmployeeDto2;

    @BeforeEach
    void setUp() {
        // Setup test account
        testAccount = new Account();
        testAccount.setAccountId(1);
        testAccount.setAccountName("employee1");
        testAccount.setEmail("employee1@email.com");
        testAccount.setPhoneNumber("0123456789");
        testAccount.setDeletedAt(null);

        // Setup test employee entity
        testEmployee = new Employee();
        testEmployee.setEmployeeId(1);
        testEmployee.setEmployeeType(1);
        testEmployee.setAccount(testAccount);
        testEmployee.setDeletedAt(null);

        // Setup test employee DTO
        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setEmployeeId(1);
        testEmployeeDto.setEmployeeType(1);
        testEmployeeDto.setAccountName("employee1");
        testEmployeeDto.setEmail("employee1@email.com");
        testEmployeeDto.setPhoneNumber("0123456789");

        // Setup second test employee
        testEmployee2 = new Employee();
        testEmployee2.setEmployeeId(2);
        testEmployee2.setEmployeeType(2);
        testEmployee2.setDeletedAt(null);

        testEmployeeDto2 = new EmployeeDto();
        testEmployeeDto2.setEmployeeId(2);
        testEmployeeDto2.setEmployeeType(2);
    }

    // ================ GET ALL EMPLOYEES TESTS ================

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_Success_ReturnsEmployeeList() {
        // Given
        List<Employee> mockEmployees = Arrays.asList(testEmployee, testEmployee2);

        when(employeeRepository.findAll()).thenReturn(mockEmployees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);
        when(employeeMapper.toDto(testEmployee2)).thenReturn(testEmployeeDto2);

        // When
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getEmployeeId());
        assertEquals(2, result.get(1).getEmployeeId());
        verify(employeeRepository).findAll();
        verify(employeeMapper, times(2)).toDto(any(Employee.class));
    }

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).findAll();
        verify(employeeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findAll()).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getAllEmployees());
        assertEquals("Database connection lost", exception.getMessage());
        verify(employeeRepository).findAll();
        verify(employeeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_MapperException_PropagatesException() {
        // Given
        List<Employee> mockEmployees = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(mockEmployees);
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getAllEmployees());
        assertEquals("Mapping error", exception.getMessage());
        verify(employeeRepository).findAll();
        verify(employeeMapper).toDto(testEmployee);
    }

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_LargeDataset_HandlesEfficiently() {
        // Given - Simulate large dataset
        List<Employee> largeEmployeeList = Arrays.asList(testEmployee, testEmployee2, testEmployee, testEmployee2, testEmployee);
        when(employeeRepository.findAll()).thenReturn(largeEmployeeList);
        when(employeeMapper.toDto(any(Employee.class))).thenReturn(testEmployeeDto);

        // When
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(employeeRepository).findAll();
        verify(employeeMapper, times(5)).toDto(any(Employee.class));
    }

    @Test
    @Tag("getAllEmployees")
    void testGetAllEmployees_SingleEmployee_ReturnsListWithOneElement() {
        // Given
        List<Employee> singleEmployee = Arrays.asList(testEmployee);
        when(employeeRepository.findAll()).thenReturn(singleEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        List<EmployeeDto> result = employeeService.getAllEmployees();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEmployeeId());
        verify(employeeRepository).findAll();
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ GET EMPLOYEE BY ID TESTS ================

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_Success_ReturnsEmployeeDto() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.getEmployeeById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getEmployeeId());
        assertEquals(1, result.getEmployeeType());
        assertEquals("employee1@email.com", result.getEmail());
        verify(employeeRepository).findActiveById(1);
        verify(employeeMapper).toDto(testEmployee);
    }

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getEmployeeById(999));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
        verify(employeeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_NullId_HandledByRepository() {
        // Given
        when(employeeRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getEmployeeById(null));
        assertTrue(exception.getMessage().contains("Employee not found with id: null"));
        verify(employeeRepository).findActiveById(null);
    }

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getEmployeeById(1));
        assertEquals("Database connection failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_MapperException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getEmployeeById(1));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeMapper).toDto(testEmployee);
    }

    @Test
    @Tag("getEmployeeById")
    void testGetEmployeeById_DifferentEmployeeType_ReturnsCorrectDto() {
        // Given
        testEmployee.setEmployeeType(5); // Admin role
        testEmployeeDto.setEmployeeType(5);
        
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.getEmployeeById(1);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getEmployeeType());
        verify(employeeRepository).findActiveById(1);
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ GET CURRENT EMPLOYEE TESTS ================

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_Success_ReturnsEmployeeDto() {
        // Given
        when(accountRepository.findByEmail("employee1@email.com")).thenReturn(Optional.of(testAccount));
        when(employeeRepository.findByAccountId(1)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.getCurrentEmployee("employee1@email.com");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getEmployeeId());
        assertEquals("employee1@email.com", result.getEmail());
        verify(accountRepository).findByEmail("employee1@email.com");
        verify(employeeRepository).findByAccountId(1);
        verify(employeeMapper).toDto(testEmployee);
    }

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_AccountNotFound_ThrowsRuntimeException() {
        // Given
        when(accountRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getCurrentEmployee("nonexistent@email.com"));
        assertEquals("Account not found with username: nonexistent@email.com", exception.getMessage());
        verify(accountRepository).findByEmail("nonexistent@email.com");
        verify(employeeRepository, never()).findByAccountId(any());
    }

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_EmployeeNotFound_ThrowsRuntimeException() {
        // Given
        when(accountRepository.findByEmail("employee1@email.com")).thenReturn(Optional.of(testAccount));
        when(employeeRepository.findByAccountId(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getCurrentEmployee("employee1@email.com"));
        assertEquals("Employee not found for account: employee1@email.com", exception.getMessage());
        verify(accountRepository).findByEmail("employee1@email.com");
        verify(employeeRepository).findByAccountId(1);
        verify(employeeMapper, never()).toDto(any());
    }

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_NullUsername_HandledByRepository() {
        // Given
        when(accountRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getCurrentEmployee(null));
        assertEquals("Account not found with username: null", exception.getMessage());
        verify(accountRepository).findByEmail(null);
    }

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_EmptyUsername_HandledByRepository() {
        // Given
        when(accountRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getCurrentEmployee(""));
        assertEquals("Account not found with username: ", exception.getMessage());
        verify(accountRepository).findByEmail("");
    }

    @Test
    @Tag("getCurrentEmployee")
    void testGetCurrentEmployee_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findByEmail("employee1@email.com")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.getCurrentEmployee("employee1@email.com"));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findByEmail("employee1@email.com");
    }

    // ================ UPDATE EMPLOYEE TESTS ================

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_Success_ReturnsUpdatedEmployeeDto() {
        // Given
        EmployeeDto updateRequest = new EmployeeDto();
        updateRequest.setEmployeeType(3);
        updateRequest.setAccountName("updatedName");
        updateRequest.setEmail("updated@email.com");
        updateRequest.setPhoneNumber("0987654321");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId(1);
        updatedEmployee.setEmployeeType(3);
        updatedEmployee.setAccount(testAccount);

        EmployeeDto resultDto = new EmployeeDto();
        resultDto.setEmployeeId(1);
        resultDto.setEmployeeType(3);
        resultDto.setAccountName("updatedName");
        resultDto.setEmail("updated@email.com");
        resultDto.setPhoneNumber("0987654321");

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeRepository.save(testEmployee)).thenReturn(updatedEmployee);
        when(employeeMapper.toDto(updatedEmployee)).thenReturn(resultDto);

        // When
        EmployeeDto result = employeeService.updateEmployee(1, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getEmployeeType());
        assertEquals("updatedName", result.getAccountName());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("0987654321", result.getPhoneNumber());
        verify(employeeRepository).findActiveById(1);
        verify(accountRepository).save(testAccount);
        verify(employeeRepository).save(testEmployee);
        verify(employeeMapper).toDto(updatedEmployee);
        
        // Verify the original entities were modified
        assertEquals(3, testEmployee.getEmployeeType());
        assertEquals("updatedName", testAccount.getAccountName());
        assertEquals("updated@email.com", testAccount.getEmail());
        assertEquals("0987654321", testAccount.getPhoneNumber());
    }

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_PartialUpdate_UpdatesOnlyProvidedFields() {
        // Given
        EmployeeDto partialUpdate = new EmployeeDto();
        partialUpdate.setEmployeeType(2);
        // Other fields are null

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        String originalEmail = testAccount.getEmail();
        String originalName = testAccount.getAccountName();

        // When
        EmployeeDto result = employeeService.updateEmployee(1, partialUpdate);

        // Then
        assertNotNull(result);
        assertEquals(2, testEmployee.getEmployeeType());
        assertEquals(originalEmail, testAccount.getEmail()); // Should remain unchanged
        assertEquals(originalName, testAccount.getAccountName()); // Should remain unchanged
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateEmployee(999, testEmployeeDto));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_NullAccount_HandlesGracefully() {
        // Given
        testEmployee.setAccount(null);
        EmployeeDto updateRequest = new EmployeeDto();
        updateRequest.setEmployeeType(2);

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.updateEmployee(1, updateRequest);

        // Then
        assertNotNull(result);
        verify(employeeRepository).findActiveById(1);
        verify(accountRepository, never()).save(any()); // No account to save
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeRepository.save(testEmployee)).thenThrow(new RuntimeException("Update failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateEmployee(1, testEmployeeDto));
        assertEquals("Update failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateEmployee")
    void testUpdateEmployee_MapperException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateEmployee(1, testEmployeeDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ DELETE EMPLOYEE TESTS ================

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_Success_SetsDeletedAt() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);

        // When
        employeeService.deleteEmployee(1);

        // Then
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(argThat(employee -> employee.getDeletedAt() != null));
        assertNotNull(testEmployee.getDeletedAt());
        assertTrue(testEmployee.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deleteEmployee(999));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_AlreadyDeleted_UpdatesDeletedAt() {
        // Given - employee already has deletedAt set
        testEmployee.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);

        // When
        employeeService.deleteEmployee(1);

        // Then - should still update deletedAt to current time
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        assertTrue(testEmployee.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_RepositoryFindException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deleteEmployee(1));
        assertEquals("Database error", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_RepositorySaveException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deleteEmployee(1));
        assertEquals("Save operation failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("deleteEmployee")
    void testDeleteEmployee_NullId_HandledByRepository() {
        // Given
        when(employeeRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deleteEmployee(null));
        assertTrue(exception.getMessage().contains("Employee not found with id: null"));
        verify(employeeRepository).findActiveById(null);
    }

    // ================ UPDATE ROLE TESTS ================

    @Test
    @Tag("updateRole")
    void testUpdateRole_Success_ReturnsUpdatedEmployeeDto() {
        // Given
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId(1);
        updatedEmployee.setEmployeeType(5); // New role

        EmployeeDto resultDto = new EmployeeDto();
        resultDto.setEmployeeId(1);
        resultDto.setEmployeeType(5);

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(updatedEmployee);
        when(employeeMapper.toDto(updatedEmployee)).thenReturn(resultDto);

        // When
        EmployeeDto result = employeeService.updateRole(1, 5);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getEmployeeType());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(employeeMapper).toDto(updatedEmployee);
        
        // Verify the original entity was modified
        assertEquals(5, testEmployee.getEmployeeType());
    }

    @Test
    @Tag("updateRole")
    void testUpdateRole_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateRole(999, 2));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @Tag("updateRole")
    void testUpdateRole_NullRole_UpdatesWithNullRole() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.updateRole(1, null);

        // Then
        assertNotNull(result);
        assertNull(testEmployee.getEmployeeType());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateRole")
    void testUpdateRole_InvalidRole_AllowedByService() {
        // Given - Service doesn't validate role values
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.updateRole(1, 999);

        // Then
        assertNotNull(result);
        assertEquals(999, testEmployee.getEmployeeType());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateRole")
    void testUpdateRole_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenThrow(new RuntimeException("Role update failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateRole(1, 2));
        assertEquals("Role update failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("updateRole")
    void testUpdateRole_MapperException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.updateRole(1, 2));
        assertEquals("Mapping failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ ACTIVATE EMPLOYEE TESTS ================

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_Success_ReturnsActivatedEmployeeDto() {
        // Given
        testEmployee.setDeletedAt(LocalDateTime.now().minusDays(1)); // Initially deleted
        testAccount.setDeletedAt(LocalDateTime.now().minusDays(1)); // Initially deleted

        Employee activatedEmployee = new Employee();
        activatedEmployee.setEmployeeId(1);
        activatedEmployee.setDeletedAt(null);
        activatedEmployee.setAccount(testAccount);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(activatedEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(activatedEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.activateEmployee(1);

        // Then
        assertNotNull(result);
        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
        verify(employeeMapper).toDto(activatedEmployee);
        
        // Verify both employee and account are activated
        assertNull(testEmployee.getDeletedAt());
        assertNull(testAccount.getDeletedAt());
    }

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.activateEmployee(999));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findById(999);
        verify(employeeRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_AlreadyActive_StillActivates() {
        // Given - employee already active
        testEmployee.setDeletedAt(null);
        testAccount.setDeletedAt(null);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.activateEmployee(1);

        // Then
        assertNotNull(result);
        assertNull(testEmployee.getDeletedAt());
        assertNull(testAccount.getDeletedAt());
        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenThrow(new RuntimeException("Activation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.activateEmployee(1));
        assertEquals("Activation failed", exception.getMessage());
        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_AccountRepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenThrow(new RuntimeException("Account activation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.activateEmployee(1));
        assertEquals("Account activation failed", exception.getMessage());
        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("activateEmployee")
    void testActivateEmployee_MapperException_PropagatesException() {
        // Given
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.activateEmployee(1));
        assertEquals("Mapping failed", exception.getMessage());
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ DEACTIVATE EMPLOYEE TESTS ================

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_Success_ReturnsDeactivatedEmployeeDto() {
        // Given
        Employee deactivatedEmployee = new Employee();
        deactivatedEmployee.setEmployeeId(1);
        deactivatedEmployee.setDeletedAt(LocalDateTime.now());
        deactivatedEmployee.setAccount(testAccount);

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(deactivatedEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(deactivatedEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.deactivateEmployee(1);

        // Then
        assertNotNull(result);
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
        verify(employeeMapper).toDto(deactivatedEmployee);
        
        // Verify both employee and account are deactivated
        assertNotNull(testEmployee.getDeletedAt());
        assertNotNull(testAccount.getDeletedAt());
    }

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_NotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deactivateEmployee(999));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
        verify(employeeRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_AlreadyDeactivated_UpdatesDeletedAt() {
        // Given - employee already has deletedAt set
        testEmployee.setDeletedAt(LocalDateTime.now().minusDays(1));
        testAccount.setDeletedAt(LocalDateTime.now().minusDays(1));

        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        // When
        EmployeeDto result = employeeService.deactivateEmployee(1);

        // Then - should update deletedAt to current time
        assertNotNull(result);
        assertTrue(testEmployee.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(testAccount.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenThrow(new RuntimeException("Deactivation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deactivateEmployee(1));
        assertEquals("Deactivation failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_AccountRepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenThrow(new RuntimeException("Account deactivation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deactivateEmployee(1));
        assertEquals("Account deactivation failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
        verify(employeeRepository).save(testEmployee);
        verify(accountRepository).save(testAccount);
    }

    @Test
    @Tag("deactivateEmployee")
    void testDeactivateEmployee_MapperException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(employeeMapper.toDto(testEmployee)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.deactivateEmployee(1));
        assertEquals("Mapping failed", exception.getMessage());
        verify(employeeMapper).toDto(testEmployee);
    }

    // ================ IS EMPLOYEE ACTIVE TESTS ================

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_ActiveEmployee_ReturnsTrue() {
        // Given - employee is active (deletedAt is null)
        testEmployee.setDeletedAt(null);
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));

        // When
        boolean result = employeeService.isEmployeeActive(1);

        // Then
        assertTrue(result);
        verify(employeeRepository).findActiveById(1);
    }

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_InactiveEmployee_ReturnsFalse() {
        // Given - employee is inactive (deletedAt is set)
        testEmployee.setDeletedAt(LocalDateTime.now());
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));

        // When
        boolean result = employeeService.isEmployeeActive(1);

        // Then
        assertFalse(result);
        verify(employeeRepository).findActiveById(1);
    }

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_EmployeeNotFound_ThrowsRuntimeException() {
        // Given
        when(employeeRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.isEmployeeActive(999));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository).findActiveById(999);
    }

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_NullId_HandledByRepository() {
        // Given
        when(employeeRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.isEmployeeActive(null));
        assertTrue(exception.getMessage().contains("Employee not found with id: null"));
        verify(employeeRepository).findActiveById(null);
    }

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_RepositoryException_PropagatesException() {
        // Given
        when(employeeRepository.findActiveById(1)).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> employeeService.isEmployeeActive(1));
        assertEquals("Database query failed", exception.getMessage());
        verify(employeeRepository).findActiveById(1);
    }

    @Test
    @Tag("isEmployeeActive")
    void testIsEmployeeActive_RecentlyDeletedEmployee_ReturnsFalse() {
        // Given - employee was recently deleted
        testEmployee.setDeletedAt(LocalDateTime.now().minusMinutes(5));
        when(employeeRepository.findActiveById(1)).thenReturn(Optional.of(testEmployee));

        // When
        boolean result = employeeService.isEmployeeActive(1);

        // Then
        assertFalse(result);
        verify(employeeRepository).findActiveById(1);
    }
}