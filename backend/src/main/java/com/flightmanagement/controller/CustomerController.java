package com.flightmanagement.controller;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Integer id, @RequestBody CustomerDto customerDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(updatedCustomer);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        CustomerDto customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }
    
    @PutMapping("/{id}/score/{score}")
    public ResponseEntity<Void> updateCustomerScore(@PathVariable Integer id, @PathVariable Integer score) {
        customerService.updateCustomerScore(id, score);
        return ResponseEntity.noContent().build();
    }
}
