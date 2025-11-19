package com.flightmanagement.service;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.mapper.CustomerMapper;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.impl.CustomerServiceImpl;
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
 * Test class for CustomerService - Core function for Customer Management
 * 
 * Available Tags:
 * - getCustomerById: Tests for retrieving customer by ID
 * - getAllCustomers: Tests for retrieving all customers
 * - createCustomer: Tests for customer creation
 * - updateCustomer: Tests for customer updates
 * - deleteCustomer: Tests for customer deletion (soft delete)
 * - getCustomerByEmail: Tests for retrieving customer by email
 * - updateCustomerScore: Tests for customer score updates
 * - getCustomerScore: Tests for retrieving customer score
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CustomerDto testCustomerDto;
    private Customer testCustomer2;
    private CustomerDto testCustomerDto2;

    @BeforeEach
    void setUp() {
        // Setup test customer entity
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setScore(100);
        testCustomer.setDeletedAt(null);

        // Setup test customer DTO
        testCustomerDto = new CustomerDto();
        testCustomerDto.setCustomerId(1);
        testCustomerDto.setScore(100);

        // Setup second test customer
        testCustomer2 = new Customer();
        testCustomer2.setCustomerId(2);
        testCustomer2.setScore(250);
        testCustomer2.setDeletedAt(null);

        testCustomerDto2 = new CustomerDto();
        testCustomerDto2.setCustomerId(2);
        testCustomerDto2.setScore(250);
    }

    // ================ GET CUSTOMER BY ID TESTS ================

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_Success_ReturnsCustomerDto() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCustomerId());
        assertEquals(100, result.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerById(999));
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository).findActiveById(999);
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_NullId_HandledByRepository() {
        // Given
        when(customerRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerById(null));
        assertTrue(exception.getMessage().contains("Customer not found with id: null"));
        verify(customerRepository).findActiveById(null);
    }

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerById(1));
        assertEquals("Database connection failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_MapperException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerById(1));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    @Tag("getCustomerById")
    void testGetCustomerById_ValidIdHighScore_ReturnsCustomerWithHighScore() {
        // Given
        testCustomer.setScore(500);
        testCustomerDto.setScore(500);
        
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerById(1);

        // Then
        assertNotNull(result);
        assertEquals(500, result.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerMapper).toDto(testCustomer);
    }

    // ================ GET ALL CUSTOMERS TESTS ================

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_Success_ReturnsCustomerList() {
        // Given
        List<Customer> mockCustomers = Arrays.asList(testCustomer, testCustomer2);

        when(customerRepository.findAllActive()).thenReturn(mockCustomers);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);
        when(customerMapper.toDto(testCustomer2)).thenReturn(testCustomerDto2);

        // When
        List<CustomerDto> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getCustomerId());
        assertEquals(2, result.get(1).getCustomerId());
        verify(customerRepository).findAllActive();
        verify(customerMapper, times(2)).toDto(any(Customer.class));
    }

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_EmptyDatabase_ReturnsEmptyList() {
        // Given
        when(customerRepository.findAllActive()).thenReturn(Collections.emptyList());

        // When
        List<CustomerDto> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).findAllActive();
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findAllActive()).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getAllCustomers());
        assertEquals("Database connection lost", exception.getMessage());
        verify(customerRepository).findAllActive();
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_MapperException_PropagatesException() {
        // Given
        List<Customer> mockCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findAllActive()).thenReturn(mockCustomers);
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getAllCustomers());
        assertEquals("Mapping error", exception.getMessage());
        verify(customerRepository).findAllActive();
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_LargeDataset_HandlesEfficiently() {
        // Given - Simulate large dataset
        List<Customer> largeCustomerList = Arrays.asList(testCustomer, testCustomer2, testCustomer, testCustomer2, testCustomer);

        when(customerRepository.findAllActive()).thenReturn(largeCustomerList);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        // When
        List<CustomerDto> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(customerRepository).findAllActive();
        verify(customerMapper, times(5)).toDto(any(Customer.class));
    }

    @Test
    @Tag("getAllCustomers")
    void testGetAllCustomers_SingleCustomer_ReturnsListWithOneElement() {
        // Given
        List<Customer> singleCustomer = Arrays.asList(testCustomer);
        when(customerRepository.findAllActive()).thenReturn(singleCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        List<CustomerDto> result = customerService.getAllCustomers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCustomerId());
        verify(customerRepository).findAllActive();
        verify(customerMapper).toDto(testCustomer);
    }

    // ================ CREATE CUSTOMER TESTS ================

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_Success_ReturnsCreatedCustomerDto() {
        // Given
        CustomerDto inputDto = new CustomerDto();
        inputDto.setScore(150);

        Customer mappedCustomer = new Customer();
        mappedCustomer.setScore(150);

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(3);
        savedCustomer.setScore(0); // Default score
        savedCustomer.setDeletedAt(null);

        CustomerDto resultDto = new CustomerDto();
        resultDto.setCustomerId(3);
        resultDto.setScore(0);

        when(customerMapper.toEntity(inputDto)).thenReturn(mappedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toDto(savedCustomer)).thenReturn(resultDto);

        // When
        CustomerDto result = customerService.createCustomer(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getCustomerId());
        assertEquals(0, result.getScore()); // Default score should be 0
        verify(customerMapper).toEntity(inputDto);
        verify(customerRepository).save(argThat(customer -> 
            customer.getDeletedAt() == null && customer.getScore().equals(0)));
        verify(customerMapper).toDto(savedCustomer);
    }

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_NullInput_HandledByMapper() {
        // Given
        when(customerMapper.toEntity(null)).thenThrow(new RuntimeException("Input cannot be null"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.createCustomer(null));
        assertEquals("Input cannot be null", exception.getMessage());
        verify(customerMapper).toEntity(null);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_RepositoryException_PropagatesException() {
        // Given
        when(customerMapper.toEntity(testCustomerDto)).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Database save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.createCustomer(testCustomerDto));
        assertEquals("Database save failed", exception.getMessage());
        verify(customerMapper).toEntity(testCustomerDto);
        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_MapperToEntityException_PropagatesException() {
        // Given
        when(customerMapper.toEntity(testCustomerDto)).thenThrow(new RuntimeException("Entity mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.createCustomer(testCustomerDto));
        assertEquals("Entity mapping failed", exception.getMessage());
        verify(customerMapper).toEntity(testCustomerDto);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_MapperToDtoException_PropagatesException() {
        // Given
        when(customerMapper.toEntity(testCustomerDto)).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.createCustomer(testCustomerDto));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(customerMapper).toEntity(testCustomerDto);
        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    @Tag("createCustomer")
    void testCreateCustomer_ValidInputWithHighScore_SetsDefaultScore() {
        // Given
        CustomerDto inputDto = new CustomerDto();
        inputDto.setScore(9999); // High score input

        Customer mappedCustomer = new Customer();
        mappedCustomer.setScore(9999);

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(4);
        savedCustomer.setScore(0); // Service sets default score
        savedCustomer.setDeletedAt(null);

        when(customerMapper.toEntity(inputDto)).thenReturn(mappedCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toDto(savedCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.createCustomer(inputDto);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(argThat(customer -> customer.getScore().equals(0)));
    }

    // ================ UPDATE CUSTOMER TESTS ================

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_Success_ReturnsUpdatedCustomerDto() {
        // Given
        CustomerDto updateDto = new CustomerDto();
        updateDto.setScore(200);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerId(1);
        updatedCustomer.setScore(200);

        CustomerDto resultDto = new CustomerDto();
        resultDto.setCustomerId(1);
        resultDto.setScore(200);

        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.toDto(updatedCustomer)).thenReturn(resultDto);

        // When
        CustomerDto result = customerService.updateCustomer(1, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        verify(customerMapper).toDto(updatedCustomer);
        
        // Verify the original entity was modified
        assertEquals(200, testCustomer.getScore());
    }

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomer(999, testCustomerDto));
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository).findActiveById(999);
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_NullScore_UpdatesWithNullScore() {
        // Given
        CustomerDto nullScoreDto = new CustomerDto();
        nullScoreDto.setScore(null);

        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.updateCustomer(1, nullScoreDto);

        // Then
        assertNotNull(result);
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        
        // Verify null score was set
        assertNull(testCustomer.getScore());
    }

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_ZeroScore_AllowedByService() {
        // Given
        CustomerDto zeroScoreDto = new CustomerDto();
        zeroScoreDto.setScore(0);

        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.updateCustomer(1, zeroScoreDto);

        // Then
        assertNotNull(result);
        assertEquals(0, testCustomer.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenThrow(new RuntimeException("Database save failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomer(1, testCustomerDto));
        assertEquals("Database save failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("updateCustomer")
    void testUpdateCustomer_MapperException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomer(1, testCustomerDto));
        assertEquals("Mapping failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        verify(customerMapper).toDto(testCustomer);
    }

    // ================ DELETE CUSTOMER TESTS ================

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_Success_SetsDeletedAt() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        customerService.deleteCustomer(1);

        // Then
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(argThat(customer -> customer.getDeletedAt() != null));
        assertNotNull(testCustomer.getDeletedAt());
        assertTrue(testCustomer.getDeletedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.deleteCustomer(999));
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository).findActiveById(999);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_AlreadyDeleted_UpdatesDeletedAt() {
        // Given - customer already has deletedAt set
        testCustomer.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        // When
        customerService.deleteCustomer(1);

        // Then - should still update deletedAt to current time
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        assertTrue(testCustomer.getDeletedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_RepositoryFindException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.deleteCustomer(1));
        assertEquals("Database connection lost", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_RepositorySaveException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenThrow(new RuntimeException("Save operation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.deleteCustomer(1));
        assertEquals("Save operation failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("deleteCustomer")
    void testDeleteCustomer_NullId_HandledByRepository() {
        // Given
        when(customerRepository.findActiveById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.deleteCustomer(null));
        assertTrue(exception.getMessage().contains("Customer not found with id: null"));
        verify(customerRepository).findActiveById(null);
    }

    // ================ GET CUSTOMER BY EMAIL TESTS ================

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_Success_ReturnsCustomerDto() {
        // Given
        when(customerRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerByEmail("test@email.com");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCustomerId());
        assertEquals(100, result.getScore());
        verify(customerRepository).findByEmail("test@email.com");
        verify(customerMapper).toDto(testCustomer);
    }

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail("nonexistent@email.com"));
        assertEquals("Customer not found with email: nonexistent@email.com", exception.getMessage());
        verify(customerRepository).findByEmail("nonexistent@email.com");
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_NullEmail_HandledByRepository() {
        // Given
        when(customerRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail(null));
        assertEquals("Customer not found with email: null", exception.getMessage());
        verify(customerRepository).findByEmail(null);
    }

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_EmptyEmail_HandledByRepository() {
        // Given
        when(customerRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail(""));
        assertEquals("Customer not found with email: ", exception.getMessage());
        verify(customerRepository).findByEmail("");
    }

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findByEmail("test@email.com")).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail("test@email.com"));
        assertEquals("Database query failed", exception.getMessage());
        verify(customerRepository).findByEmail("test@email.com");
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("getCustomerByEmail")
    void testGetCustomerByEmail_MapperException_PropagatesException() {
        // Given
        when(customerRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail("test@email.com"));
        assertEquals("Mapping failed", exception.getMessage());
        verify(customerRepository).findByEmail("test@email.com");
        verify(customerMapper).toDto(testCustomer);
    }

    // ================ UPDATE CUSTOMER SCORE TESTS ================

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_Success_ReturnsUpdatedCustomerDto() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.updateCustomerScore(1, 300);

        // Then
        assertNotNull(result);
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        verify(customerMapper).toDto(testCustomer);
        
        // Verify the score was updated
        assertEquals(300, testCustomer.getScore());
    }

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findActiveById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomerScore(999, 300));
        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository).findActiveById(999);
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_NullScore_UpdatesWithNullScore() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.updateCustomerScore(1, null);

        // Then
        assertNotNull(result);
        assertNull(testCustomer.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_NegativeScore_AllowedByService() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenReturn(testCustomerDto);

        // When
        CustomerDto result = customerService.updateCustomerScore(1, -50);

        // Then
        assertNotNull(result);
        assertEquals(-50, testCustomer.getScore());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenThrow(new RuntimeException("Score update failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomerScore(1, 300));
        assertEquals("Score update failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @Tag("updateCustomerScore")
    void testUpdateCustomerScore_MapperException_PropagatesException() {
        // Given
        when(customerRepository.findActiveById(1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDto(testCustomer)).thenThrow(new RuntimeException("DTO mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.updateCustomerScore(1, 300));
        assertEquals("DTO mapping failed", exception.getMessage());
        verify(customerRepository).findActiveById(1);
        verify(customerRepository).save(testCustomer);
        verify(customerMapper).toDto(testCustomer);
    }

    // ================ GET CUSTOMER SCORE TESTS ================

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_Success_ReturnsScore() {
        // Given
        when(customerRepository.findScoreById(1)).thenReturn(Optional.of(100));

        // When
        Integer result = customerService.getCustomerScore(1);

        // Then
        assertNotNull(result);
        assertEquals(100, result);
        verify(customerRepository).findScoreById(1);
    }

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_NotFound_ThrowsRuntimeException() {
        // Given
        when(customerRepository.findScoreById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerScore(999));
        assertEquals("Customer score not found with id: 999", exception.getMessage());
        verify(customerRepository).findScoreById(999);
    }

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_ZeroScore_ReturnsZero() {
        // Given
        when(customerRepository.findScoreById(1)).thenReturn(Optional.of(0));

        // When
        Integer result = customerService.getCustomerScore(1);

        // Then
        assertEquals(0, result);
        verify(customerRepository).findScoreById(1);
    }

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_HighScore_ReturnsHighScore() {
        // Given
        when(customerRepository.findScoreById(1)).thenReturn(Optional.of(999999));

        // When
        Integer result = customerService.getCustomerScore(1);

        // Then
        assertEquals(999999, result);
        verify(customerRepository).findScoreById(1);
    }

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_RepositoryException_PropagatesException() {
        // Given
        when(customerRepository.findScoreById(1)).thenThrow(new RuntimeException("Score query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerScore(1));
        assertEquals("Score query failed", exception.getMessage());
        verify(customerRepository).findScoreById(1);
    }

    @Test
    @Tag("getCustomerScore")
    void testGetCustomerScore_NullId_HandledByRepository() {
        // Given
        when(customerRepository.findScoreById(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerScore(null));
        assertEquals("Customer score not found with id: null", exception.getMessage());
        verify(customerRepository).findScoreById(null);
    }
}