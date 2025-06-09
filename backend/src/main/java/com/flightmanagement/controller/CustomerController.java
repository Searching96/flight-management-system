package com.flightmanagement.controller;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// CustomerController.java - Role-specific customer management
// CustomerController.java
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private final CustomerService customerService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') and #id == principal.id or hasRole('EMPLOYEE_SUPPORT')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}/score")
    @PreAuthorize("hasRole('EMPLOYEE_SUPPORT')")
    public ResponseEntity<Void> updateScore(@PathVariable Integer id, @RequestParam Integer score) {
        customerService.updateCustomerScore(id, score);
        return ResponseEntity.noContent().build();
    }
}
