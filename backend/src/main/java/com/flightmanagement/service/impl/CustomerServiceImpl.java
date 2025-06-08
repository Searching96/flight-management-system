package com.flightmanagement.service.impl;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.mapper.CustomerMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAllActive();
        return customerMapper.toDtoList(customers);
    }
    
    @Override
    public CustomerDto getCustomerById(Integer id) {
        Customer customer = customerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return customerMapper.toDto(customer);
    }
    
    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = customerMapper.toEntity(customerDto);
        customer.setDeletedAt(null);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }
    
    @Override
    public CustomerDto updateCustomer(Integer id, CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        existingCustomer.setScore(customerDto.getScore());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toDto(updatedCustomer);
    }
    
    @Override
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findActiveById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
    
    @Override
    public CustomerDto getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return customerMapper.toDto(customer);
    }
    
    @Override
    public void updateCustomerScore(Integer customerId, Integer score) {
        Customer customer = customerRepository.findActiveById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setScore(score);
        customerRepository.save(customer);
    }
    
    @Override
    public CustomerDto createCustomerWithAccountId(Integer accountId) {
        System.out.println("Creating customer with account ID: " + accountId);
        
        // Check if customer already exists
        Customer existingCustomer = customerRepository.findActiveById(accountId).orElse(null);
        if (existingCustomer != null) {
            System.out.println("Customer already exists with ID: " + accountId);
            return customerMapper.toDto(existingCustomer);
        }
        
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        
        // Verify this is a customer account (accountType = 1)
        if (account.getAccountType() != 1) {
            throw new RuntimeException("Account " + accountId + " is not a customer account");
        }
        
        try {
            Customer customer = new Customer();
            // Don't set customerId manually, let it be set by the foreign key relationship
            customer.setAccount(account);
            customer.setScore(0); // Default score
            customer.setDeletedAt(null);
            
            Customer savedCustomer = customerRepository.save(customer);
            System.out.println("Customer created successfully with ID: " + savedCustomer.getCustomerId());
            return customerMapper.toDto(savedCustomer);
        } catch (Exception e) {
            System.err.println("Failed to create customer for account " + accountId + ": " + e.getMessage());
            throw new RuntimeException("Failed to create customer record", e);
        }
    }
}
