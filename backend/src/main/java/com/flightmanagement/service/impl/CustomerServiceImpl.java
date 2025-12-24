package com.flightmanagement.service.impl;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.mapper.CustomerMapper;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.AuditLogService;
import com.flightmanagement.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final AuditLogService auditLogService;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper, AuditLogService auditLogService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditLogService = auditLogService;
    }

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
    public Page<CustomerDto> getAllCustomersPaged(Pageable pageable) {
        Page<Customer> page = customerRepository.findByDeletedAtIsNull(pageable);
        return page.map(customerMapper::toDto);
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        customer.setDeletedAt(null);
        customer.setScore(0); // default score
        Customer savedCustomer = customerRepository.save(customer);
        
        // Audit log for CREATE
        String customerDescription = savedCustomer.getAccount() != null ? savedCustomer.getAccount().getAccountName() : "Customer";
        auditLogService.saveAuditLog("Customer", savedCustomer.getCustomerId().toString(), "CREATE", "customer", null, customerDescription, "system");
        
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Integer id, CustomerDto dto) {
        Customer existingCustomer = customerRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Store old values for audit logging
        String oldScore = existingCustomer.getScore() != null ? existingCustomer.getScore().toString() : null;
        
        // Update only allowed fields (e.g., score, phone, etc.)
        existingCustomer.setScore(dto.getScore());
        // Add more fields as needed

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        
        // Audit log for changed fields
        String newScore = updatedCustomer.getScore() != null ? updatedCustomer.getScore().toString() : null;
        if ((oldScore == null && newScore != null) || (oldScore != null && !oldScore.equals(newScore))) {
            auditLogService.saveAuditLog("Customer", id.toString(), "UPDATE", "score", oldScore, newScore, "system");
        }
        
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Capture entity info before delete
        String customerDescription = customer.getAccount() != null ? customer.getAccount().getAccountName() : "Customer";
        
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
        
        // Audit log for DELETE
        auditLogService.saveAuditLog("Customer", id.toString(), "DELETE", "customer", customerDescription, null, "system");
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
