package com.flightmanagement.controller;

import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.AuthMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.security.CustomUserDetails;
import com.flightmanagement.security.JwtService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Map;

// AuthController.java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDto request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(createAuthResponse(userDetails));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterDto request
    ) {
        Account newAccount = new Account();
        // Map common fields
        newAccount.setAccountName(request.getAccountName());
        newAccount.setEmail(request.getEmail());
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        newAccount.setCitizenId(request.getCitizenId());
        newAccount.setPhoneNumber(request.getPhoneNumber());
        newAccount.setAccountType(request.getAccountType());

        Account savedAccount = accountRepo.save(newAccount);

        // Handle account type-specific relationships
        if (request.getAccountType() == 1) {
            Customer customer = new Customer();
            customer.setAccount(savedAccount);
            customerRepository.save(customer);
        } else if (request.getAccountType() == 2) {
            Employee employee = new Employee();
            employee.setAccount(savedAccount);
            employee.setEmployeeType(request.getEmployeeType());
            employeeRepository.save(employee);
        }

        return ResponseEntity.ok(createAuthResponse(
                CustomUserDetails.create(savedAccount)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String request)
            throws AccessDeniedException {

        if (!jwtService.validateToken(request)) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(request);
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(createAuthResponse(CustomUserDetails.create(account)));
    }

    private AuthResponse createAuthResponse(CustomUserDetails userDetails) {
        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails.getEmail()),
                "Bearer",
                Instant.now().plusMillis(jwtService.getJwtExpirationMs()),
                authMapper.toUserDetailsDto(userDetails));
    }
}
