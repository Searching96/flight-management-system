package com.flightmanagement.service;

import com.flightmanagement.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    
    List<CustomerDto> getAllCustomers();
    
    CustomerDto getCustomerById(Integer id);
    
    CustomerDto createCustomer(CustomerDto customerDto);
    
    CustomerDto updateCustomer(Integer id, CustomerDto customerDto);
    
    void deleteCustomer(Integer id);
    
    CustomerDto getCustomerByEmail(String email);
    
    void updateCustomerScore(Integer customerId, Integer score);
    
    CustomerDto createCustomerWithAccountId(Integer accountId);
}
