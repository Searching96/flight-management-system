package com.flightmanagement.service.impl;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.mapper.CustomerMapper;
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
}
