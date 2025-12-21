package com.flightmanagement.controller;

import com.flightmanagement.dto.CustomerDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer", description = "Operations related to customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Get all customers")
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getAllCustomers(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        Page<CustomerDto> page = customerService.getAllCustomersPaged(pageable);
        ApiResponse<?> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Fetched all customers",
                page,
                null
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE_SUPPORT')")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Integer id) {
        CustomerDto customerDto = customerService.getCustomerById(id);
        ApiResponse<CustomerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Customer retrieved successfully",
                customerDto,
                null
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get customer by email")
    @GetMapping("/email")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByEmail(@RequestParam String email) {
        CustomerDto customerDto = customerService.getCustomerByEmail(email);

        ApiResponse<CustomerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Customer retrieved successfully",
                customerDto,
                null
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update customer score")
    @PutMapping("/{id}/score/{score}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateScore(@PathVariable Integer id, @PathVariable Integer score) {
        CustomerDto customer = customerService.updateCustomerScore(id, score);

        ApiResponse<CustomerDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Customer score updated successfully",
                customer,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get customer score")
    @GetMapping("/{id}/score")
    public ResponseEntity<ApiResponse<Integer>> getCustomerScore(@PathVariable Integer id) {
        Integer score = customerService.getCustomerScore(id);
        ApiResponse<Integer> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Customer score retrieved successfully",
                score,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
