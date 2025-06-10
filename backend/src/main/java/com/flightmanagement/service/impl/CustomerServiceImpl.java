package com.flightmanagement.service.impl;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.mapper.CustomerMapper;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;


    @Override
    public CustomerDto getCustomerById(Integer id) {
        return customerMapper.toDto(
                customerRepository.findActiveById(id)
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id))
        );
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAllActive()
                .stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        customer.setDeletedAt(null);
        customer.setScore(0); // default score
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Integer id, CustomerDto dto) {
        Customer existingCustomer = customerRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Update only allowed fields (e.g., score, phone, etc.)
        existingCustomer.setScore(dto.getScore());
        // Add more fields as needed

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        return customerMapper.toDto(
                customerRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email))
        );
    }

    @Override
    @Transactional
    public CustomerDto updateCustomerScore(Integer id, Integer score) {
        Customer customer = customerRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customer.setScore(score);
        customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }

    @Override
    public Integer getCustomerScore(Integer id) {
        return customerRepository.findScoreById(id)
                .orElseThrow(() -> new RuntimeException("Customer score not found with id: " + id));
    }
}
