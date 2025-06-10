package com.flightmanagement.service.impl;

import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.mapper.AccountMapper;
import com.flightmanagement.mapper.AuthMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.security.CustomUserDetails;
import com.flightmanagement.security.JwtService;
import com.flightmanagement.service.AccountService;
import com.flightmanagement.service.AuthService;

import com.flightmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public AuthResponse authenticate(LoginRequestDto request) {
        // Authenticate user credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Get user details from authentication principal
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Build the response
        return createAuthResponse(userDetails);
    }

    @Override
    public AuthResponse register(RegisterDto request) {
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

        return createAuthResponse(CustomUserDetails.create(savedAccount));
    }

    @Override
    public AuthResponse refreshToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new AccessDeniedException("Invalid refresh token");
        }

        String email = jwtService.getEmailFromToken(token);
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        CustomUserDetails userDetails = CustomUserDetails.create(account);
        return createAuthResponse(userDetails);
    }

    private AuthResponse createAuthResponse(CustomUserDetails userDetails) {
        return new AuthResponse(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails.getEmail()),
                "Bearer",
                Instant.now().plusMillis(jwtService.getJwtExpirationMs()),
                authMapper.toUserDetailsDto(userDetails));
    }

    @Override
    public AuthResponse debugLoginByName(String accountName) {
        // Get full account details (including password)
        Account account = accountRepo.findByAccountName(accountName)
                .orElseThrow(() -> new RuntimeException("Account not found with name: " + accountName));

        // Create user details without password validation
        CustomUserDetails userDetails = CustomUserDetails.create(account);

        // Generate tokens directly
        return createAuthResponse(userDetails);
    }

    @Override
    public void processForgotPassword(String email) {
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));

        String resetToken = jwtService.generatePasswordResetToken(email);
        account.setPasswordResetToken(resetToken);
        account.setPasswordResetExpiry(Instant.now().plus(15, ChronoUnit.MINUTES));
        accountRepo.save(account);

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    @Override
    public AuthResponse processPasswordReset(String token, String newPassword) {
        if (!jwtService.validatePasswordResetToken(token)) {
            throw new AccessDeniedException("Invalid token");
        }

        String email = jwtService.getEmailFromPasswordResetToken(token);
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));

        if (!token.equals(account.getPasswordResetToken())) {
            throw new AccessDeniedException("Token mismatch");
        }

        assert account.getPasswordResetExpiry() != null;
        if (account.getPasswordResetExpiry().isBefore(Instant.now())) {
            throw new AccessDeniedException("Token expired");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        account.setPasswordResetToken(null);
        account.setPasswordResetExpiry(null);
        accountRepo.save(account);

        return createAuthResponse(CustomUserDetails.create(account));
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        return jwtService.validatePasswordResetToken(token);
    }

    @Override
    public boolean validateEmail(String email) {
        return accountRepo.existsByEmailAndNotDeleted(email);
    }
}
