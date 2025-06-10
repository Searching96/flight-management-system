package com.flightmanagement.controller;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CustomerController.java - Role-specific customer management
// CustomerController.java
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE_SUPPORT')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/email")
    public ResponseEntity<CustomerDto> getCustomerByEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PutMapping("/{id}/score/{score}")
    public ResponseEntity<CustomerDto> updateScore(@PathVariable Integer id, @PathVariable Integer score) {
        CustomerDto customer = customerService.updateCustomerScore(id, score);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<Integer> getCustomerScore(@PathVariable Integer id) {
        Integer score = customerService.getCustomerScore(id);
        return ResponseEntity.ok(score);
    }
}
