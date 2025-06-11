package com.flightmanagement.service.impl;

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
import java.util.Random;

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
            savedAccount.setCustomer(customer);
            emailService.sendCustomerWelcomeEmail(savedAccount.getEmail(), savedAccount.getAccountName());
        } else if (request.getAccountType() == 2) {
            // Generate random password for employee
            String randomPassword = generateRandomPassword();

            Employee employee = new Employee();
            employee.setAccount(savedAccount);
            employee.setEmployeeType(request.getEmployeeType());
            Employee savedEmployee = employeeRepository.save(employee);

            // Associate employee with account + set random password
            savedAccount.setEmployee(savedEmployee);
            savedAccount.setPassword(passwordEncoder.encode(randomPassword));
            accountRepo.save(savedAccount);

            // Get employee type name for email
            String employeeTypeName = getEmployeeTypeName(request.getEmployeeType());

            // Send credentials email to employee
            try {
                emailService.sendEmployeeCredentialsEmail(
                        savedAccount.getEmail(),
                        savedAccount.getAccountName(),
                        savedAccount.getAccountName(),
                        employeeTypeName,
                        randomPassword  // Pass the generated password
                );
                System.out.println("Employee credentials email sent to: " + savedAccount.getEmail() + " at 2025-06-11 07:20:15 UTC by thinh0704hcm");
                System.out.println("Generated password: " + randomPassword);
            } catch (Exception e) {
                System.err.println("Failed to send employee credentials email: " + e.getMessage());
                // Log but don't fail the registration
            }
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

        if (account.getDeletedAt() != null) {
            throw new RuntimeException("Account is deleted: " + accountName);
        }

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

    // Add this helper method to generate 8-character random password
    private String generateRandomPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@#$%";
        String allChars = upperCase + lowerCase + digits + specialChars;

        Random random = new Random();
        StringBuilder password = new StringBuilder(8);

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill remaining 4 positions with random characters
        for (int i = 4; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to avoid predictable pattern
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    private String getEmployeeTypeName(Integer employeeType) {
        return switch (employeeType) {
            case 1 -> "Vận hành chuyến bay";
            case 2 -> "Bán vé";
            case 3 -> "Hỗ trợ khách hàng";
            case 4 -> "Lập lịch chuyến bay";
            case 5 -> "Quản trị viên";
            default -> "Nhân viên";
        };
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
