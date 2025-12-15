package com.flightmanagement.service;

import com.flightmanagement.dto.AuthResponse;
import com.flightmanagement.dto.LoginRequestDto;
import com.flightmanagement.dto.RegisterDto;
import com.flightmanagement.dto.UserDetailsDto;
import com.flightmanagement.entity.Account;
import com.flightmanagement.entity.Customer;
import com.flightmanagement.entity.Employee;
import com.flightmanagement.enums.AccountType;
import com.flightmanagement.enums.EmployeeType;
import com.flightmanagement.exception.ResourceNotFoundException;
import com.flightmanagement.mapper.AuthMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.repository.EmployeeRepository;
import com.flightmanagement.security.CustomUserDetails;
import com.flightmanagement.security.JwtService;
import com.flightmanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Test class for AuthService - Core function for Authentication and Authorization
 * 
 * Available Tags:
 * - authenticate: Tests for user login authentication
 * - register: Tests for user registration process
 * - refreshToken: Tests for token refresh functionality
 * - debugLoginByName: Tests for debug login by account name
 * - processForgotPassword: Tests for forgot password processing
 * - processPasswordReset: Tests for password reset with token
 * - validatePasswordResetToken: Tests for password reset token validation
 * - validateEmail: Tests for email validation and existence checking
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthMapper authMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Account testAccount;
    private Customer testCustomer;
    private Employee testEmployee;
    private LoginRequestDto testLoginRequest;
    private RegisterDto testRegisterDto;
    private CustomUserDetails testUserDetails;
    private Authentication testAuthentication;
    private AuthResponse testAuthResponse;
    private UserDetailsDto testUserDetailsDto;

    @BeforeEach
    void setUp() {
        // Setup test account
        testAccount = new Account();
        testAccount.setAccountId(1);
        testAccount.setAccountName("testuser");
        testAccount.setEmail("test@email.com");
        testAccount.setPhoneNumber("0123456789");
        testAccount.setPassword("encodedPassword");
        testAccount.setAccountType(AccountType.CUSTOMER);
        testAccount.setDeletedAt(null);

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setAccount(testAccount);
        testAccount.setCustomer(testCustomer);

        // Setup test employee
        testEmployee = new Employee();
        testEmployee.setEmployeeId(1);
        testEmployee.setEmployeeType(EmployeeType.FLIGHT_SCHEDULING);
        testEmployee.setAccount(testAccount);

        // Setup test login request
        testLoginRequest = new LoginRequestDto();
        testLoginRequest.setEmail("test@email.com");
        testLoginRequest.setPassword("password123");

        // Setup test register DTO
        testRegisterDto = new RegisterDto();
        testRegisterDto.setAccountName("newuser");
        testRegisterDto.setEmail("newuser@email.com");
        testRegisterDto.setPassword("password123");
        testRegisterDto.setPhoneNumber("0123456789");
        testRegisterDto.setCitizenId("123456789");
        testRegisterDto.setAccountType(AccountType.CUSTOMER.getValue());

        // Setup test user details
        testUserDetails = CustomUserDetails.create(testAccount);

        // Setup test authentication
        testAuthentication = mock(Authentication.class);
        lenient().when(testAuthentication.getPrincipal()).thenReturn(testUserDetails);

        // Setup test user details DTO
        testUserDetailsDto = new UserDetailsDto();
        testUserDetailsDto.setId(1);
        testUserDetailsDto.setAccountName("testuser");
        testUserDetailsDto.setEmail("test@email.com");
        testUserDetailsDto.setAccountTypeName("USER");

        // Setup test auth response
        testAuthResponse = new AuthResponse(
            "accessToken",
            "refreshToken",
            "Bearer",
            Instant.now().plusSeconds(3600),
            testUserDetailsDto
        );
    }

    // ================ AUTHENTICATE TESTS ================

    @Nested
    @DisplayName("Authenticate - Full Path Coverage")
    @Tag("authenticate")
    class AuthenticateTests {

        @Test
        @DisplayName("TC1: Đăng nhập hợp lệ - Trả về AuthResponse")
        void authenticate_LoginSuccess_ReturnsAuthResponse() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(testAuthentication);
            when(jwtService.generateAccessToken(testUserDetails)).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(testUserDetails.getEmail())).thenReturn("refreshToken");
            when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
            when(authMapper.toUserDetailsDto(testUserDetails)).thenReturn(testUserDetailsDto);

            // Act
            AuthResponse result = authService.authenticate(testLoginRequest);

            // Assert
            assertNotNull(result);
            assertEquals("accessToken", result.getAccessToken());
            assertEquals("refreshToken", result.getRefreshToken());
            assertEquals("Bearer", result.getTokenType());
            assertEquals(testUserDetailsDto, result.getUserDetails());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService).generateAccessToken(testUserDetails);
            verify(jwtService).generateRefreshToken(testUserDetails.getEmail());
            verify(authMapper).toUserDetailsDto(testUserDetails);
        }

        @Test
        @DisplayName("TC2: Sai thông tin đăng nhập - Ném BadCredentialsException")
        void authenticate_LoginFailed_BadCredentials_ThrowsException() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act + Assert
            BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.authenticate(testLoginRequest));

            assertEquals("Bad credentials", exception.getMessage());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(jwtService);
            verifyNoInteractions(authMapper);
        }
    }

    // ================ REGISTER TESTS ================

    @Nested
    @DisplayName("Register - Full Path Coverage")
    @Tag("register")
    class RegisterTests {

        @Test
        @DisplayName("TC1: Email đã tồn tại - Ném RuntimeException")
        void register_EmailExists_ThrowsRuntimeException() {
            // Arrange
            when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(true);

            // Act + Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(testRegisterDto));

            assertEquals("Email already exists", exception.getMessage());
            verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
            verify(accountRepository, never()).save(isA(Account.class));
            verifyNoInteractions(customerRepository, employeeRepository, jwtService, authMapper);
        }

        @Test
        @DisplayName("TC2: Đăng ký Employee - Sinh random pass, lưu Account + Employee")
        void register_Employee_Success() {
            // Arrange
            testRegisterDto.setAccountType(AccountType.EMPLOYEE.getValue());
            testRegisterDto.setEmployeeType(EmployeeType.FLIGHT_SCHEDULING);

            Account savedAccount = new Account();
            savedAccount.setAccountId(3);
            savedAccount.setEmail("newuser@email.com");
            savedAccount.setAccountType(AccountType.EMPLOYEE);

            Employee savedEmployee = new Employee();
            savedEmployee.setEmployeeId(3);
            savedEmployee.setEmployeeType(EmployeeType.FLIGHT_SCHEDULING);
            savedEmployee.setAccount(savedAccount);

            when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedRandomPassword");
            when(accountRepository.save(isA(Account.class))).thenReturn(savedAccount);
            when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
            when(jwtService.generateAccessToken(any(CustomUserDetails.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken("newuser@email.com")).thenReturn("refreshToken");
            when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
            when(authMapper.toUserDetailsDto(any(CustomUserDetails.class))).thenReturn(testUserDetailsDto);

            // Act
            AuthResponse result = authService.register(testRegisterDto);

            // Assert
            assertNotNull(result);
            assertEquals("accessToken", result.getAccessToken());
            assertEquals("refreshToken", result.getRefreshToken());
            verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
            verify(passwordEncoder, atLeastOnce()).encode(anyString());
            verify(accountRepository).save(isA(Account.class));
            verify(employeeRepository).save(any(Employee.class));
            verify(jwtService).generateAccessToken(any(CustomUserDetails.class));
            verify(jwtService).generateRefreshToken("newuser@email.com");
            verify(authMapper).toUserDetailsDto(any(CustomUserDetails.class));
            verifyNoInteractions(customerRepository);
        }

        @Test
        @DisplayName("TC3: Đăng ký Customer - Giữ pass gốc, lưu Account + Customer")
        void register_Customer_Success() {
            // Arrange
            testRegisterDto.setAccountType(AccountType.CUSTOMER.getValue());

            Account savedAccount = new Account();
            savedAccount.setAccountId(2);
            savedAccount.setAccountType(AccountType.CUSTOMER);
            savedAccount.setEmail("newuser@email.com");

            Customer savedCustomer = new Customer();
            savedCustomer.setCustomerId(2);
            savedCustomer.setAccount(savedAccount);

            when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
            when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
            when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
            when(jwtService.generateAccessToken(any(CustomUserDetails.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken("newuser@email.com")).thenReturn("refreshToken");
            when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
            when(authMapper.toUserDetailsDto(any(CustomUserDetails.class))).thenReturn(testUserDetailsDto);

            // Act
            AuthResponse result = authService.register(testRegisterDto);

            // Assert
            assertNotNull(result);
            assertEquals("accessToken", result.getAccessToken());
            assertEquals("refreshToken", result.getRefreshToken());
            verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
            verify(passwordEncoder).encode("password123");
            verify(accountRepository).save(any(Account.class));
            verify(customerRepository).save(any(Customer.class));
            verify(jwtService).generateAccessToken(any(CustomUserDetails.class));
            verify(jwtService).generateRefreshToken("newuser@email.com");
            verify(authMapper).toUserDetailsDto(any(CustomUserDetails.class));
            verifyNoInteractions(employeeRepository);
        }

        @Test
        @DisplayName("TC4: Loại tài khoản không hợp lệ - Lưu Account rồi Throw IllegalArgumentException (ROLLBACK)")
        void register_InvalidType_ThrowsIllegalArgumentException() {
            // Arrange
            testRegisterDto.setAccountType(999); // AccountType không hợp lệ

            Account savedAccount = new Account();
            savedAccount.setAccountId(4);
            savedAccount.setAccountType(AccountType.fromValue(999));
            savedAccount.setEmail("newuser@email.com");

            when(accountRepository.existsByEmailAndNotDeleted("newuser@email.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
            when(accountRepository.save(isA(Account.class))).thenReturn(savedAccount);

            // Act + Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(testRegisterDto));

            assertTrue(exception.getMessage().contains("Invalid Account Type"));
            verify(accountRepository).existsByEmailAndNotDeleted("newuser@email.com");
            verify(passwordEncoder).encode("password123");
            verify(accountRepository).save(isA(Account.class)); // Account được lưu trước
            verifyNoInteractions(customerRepository, employeeRepository, jwtService, authMapper);
        }
    }

    // ================ REFRESH TOKEN TESTS ================

    @Test
    @Tag("refreshToken")
    void testRefreshToken_Success_ReturnsNewAuthResponse() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
        when(jwtService.generateAccessToken(any(CustomUserDetails.class))).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken("test@email.com")).thenReturn("newRefreshToken");
        when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
        when(authMapper.toUserDetailsDto(any(CustomUserDetails.class))).thenReturn(testUserDetailsDto);

        // When
        AuthResponse result = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(result);
        assertEquals("newAccessToken", result.getAccessToken());
        assertEquals("newRefreshToken", result.getRefreshToken());
        verify(jwtService).validateToken(refreshToken);
        verify(jwtService).getEmailFromToken(refreshToken);
        verify(accountRepository).findByEmail("test@email.com");
    }

    @Test
    @Tag("refreshToken")
    void testRefreshToken_InvalidToken_ThrowsAccessDeniedException() {
        // Given
        String invalidToken = "invalidToken";
        when(jwtService.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.refreshToken(invalidToken));
        assertEquals("Invalid refresh token", exception.getMessage());
        verify(jwtService).validateToken(invalidToken);
        verify(accountRepository, never()).findByEmail(any());
    }

    @Test
    @Tag("refreshToken")
    void testRefreshToken_UserNotFound_ThrowsAccessDeniedException() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenReturn("nonexistent@email.com");
        when(accountRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("User not found", exception.getMessage());
        verify(jwtService).validateToken(refreshToken);
        verify(accountRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @Tag("refreshToken")
    void testRefreshToken_NullToken_ThrowsAccessDeniedException() {
        // Given
        when(jwtService.validateToken(null)).thenReturn(false);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.refreshToken(null));
        assertEquals("Invalid refresh token", exception.getMessage());
        verify(jwtService).validateToken(null);
    }

    @Test
    @Tag("refreshToken")
    void testRefreshToken_JwtServiceException_PropagatesException() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenThrow(new RuntimeException("JWT parsing failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("JWT parsing failed", exception.getMessage());
        verify(jwtService).getEmailFromToken(refreshToken);
    }

    @Test
    @Tag("refreshToken")
    void testRefreshToken_RepositoryException_PropagatesException() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("Database error", exception.getMessage());
        verify(accountRepository).findByEmail("test@email.com");
    }

    // ================ DEBUG LOGIN BY NAME TESTS ================

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_Success_ReturnsAuthResponse() {
        // Given
        when(accountRepository.findByAccountName("testuser")).thenReturn(Optional.of(testAccount));
        when(jwtService.generateAccessToken(any(CustomUserDetails.class))).thenReturn("debugAccessToken");
        when(jwtService.generateRefreshToken("test@email.com")).thenReturn("debugRefreshToken");
        when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
        when(authMapper.toUserDetailsDto(any(CustomUserDetails.class))).thenReturn(testUserDetailsDto);

        // When
        AuthResponse result = authService.debugLoginByName("testuser");

        // Then
        assertNotNull(result);
        assertEquals("debugAccessToken", result.getAccessToken());
        assertEquals("debugRefreshToken", result.getRefreshToken());
        verify(accountRepository).findByAccountName("testuser");
        verify(jwtService).generateAccessToken(any(CustomUserDetails.class));
    }

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_AccountNotFound_ThrowsRuntimeException() {
        // Given
        when(accountRepository.findByAccountName("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.debugLoginByName("nonexistent"));
        assertEquals("Account not found with name: nonexistent", exception.getMessage());
        verify(accountRepository).findByAccountName("nonexistent");
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_DeletedAccount_ThrowsRuntimeException() {
        // Given
        testAccount.setDeletedAt(LocalDateTime.now());
        when(accountRepository.findByAccountName("testuser")).thenReturn(Optional.of(testAccount));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.debugLoginByName("testuser"));
        assertEquals("Account is deleted: testuser", exception.getMessage());
        verify(accountRepository).findByAccountName("testuser");
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_NullAccountName_HandledByRepository() {
        // Given
        when(accountRepository.findByAccountName(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.debugLoginByName(null));
        assertTrue(exception.getMessage().contains("Account not found with name: null"));
        verify(accountRepository).findByAccountName(null);
    }

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_EmptyAccountName_HandledByRepository() {
        // Given
        when(accountRepository.findByAccountName("")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.debugLoginByName(""));
        assertTrue(exception.getMessage().contains("Account not found with name: "));
        verify(accountRepository).findByAccountName("");
    }

    @Test
    @Tag("debugLoginByName")
    void testDebugLoginByName_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.findByAccountName("testuser")).thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.debugLoginByName("testuser"));
        assertEquals("Database query failed", exception.getMessage());
        verify(accountRepository).findByAccountName("testuser");
    }

    // ================ PROCESS FORGOT PASSWORD TESTS ================

    @Nested
    @DisplayName("ProcessForgotPassword - Full Path Coverage")
    @Tag("processForgotPassword")
    class ProcessForgotPasswordTests {

        @Test
        @DisplayName("TC1: Email không tồn tại - Ném ResourceNotFoundException")
        void processForgotPassword_EmailNotFound_ThrowsResourceNotFoundException() {
            // Arrange
            when(accountRepository.findByEmail("wrong@email.com")).thenReturn(Optional.empty());

            // Act + Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.processForgotPassword("wrong@email.com", "0123456789"));

            assertEquals("Account not found with email: wrong@email.com", exception.getMessage());
            verify(accountRepository).findByEmail("wrong@email.com");
            verifyNoInteractions(jwtService, emailService);
            verify(accountRepository, never()).save(isA(Account.class));
        }

        @Test
        @DisplayName("TC2: Số điện thoại không khớp - Ném ResourceNotFoundException")
        void processForgotPassword_PhoneMismatch_ThrowsResourceNotFoundException() {
            // Arrange
            String wrongPhone = "9876543210";
            when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));

            // Act + Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.processForgotPassword("test@email.com", wrongPhone));

            assertEquals("Account not found with provided phone number", exception.getMessage());
            verify(accountRepository).findByEmail("test@email.com");
            verifyNoInteractions(jwtService, emailService);
            verify(accountRepository, never()).save(isA(Account.class));
        }

        @Test
        @DisplayName("TC3: Email hợp lệ, số điện thoại khớp - Lưu token và gửi email")
        void processForgotPassword_ValidEmailAndPhone_Success() {
            // Arrange
            String realPhone = testAccount.getPhoneNumber();
            when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
            when(jwtService.generatePasswordResetToken("test@email.com")).thenReturn("resetToken123");
            when(accountRepository.save(isA(Account.class))).thenReturn(testAccount);

            // Act
            authService.processForgotPassword("test@email.com", realPhone);

            // Assert
            verify(accountRepository).findByEmail("test@email.com");
            verify(jwtService).generatePasswordResetToken("test@email.com");
            verify(accountRepository).save(argThat(account ->
                "resetToken123".equals(account.getPasswordResetToken()) &&
                account.getPasswordResetExpiry() != null));
            verify(emailService).sendPasswordResetEmail("test@email.com", "resetToken123");
        }
    }

    // ================ PROCESS PASSWORD RESET TESTS ================

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_Success_ResetsPasswordAndReturnsAuthResponse() {
        // Given
        String resetToken = "validResetToken";
        String newPassword = "newPassword123";
        testAccount.setPasswordResetToken(resetToken);
        testAccount.setPasswordResetExpiry(Instant.now().plus(10, ChronoUnit.MINUTES));

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.getEmailFromPasswordResetToken(resetToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(accountRepository.save(testAccount)).thenReturn(testAccount);
        when(jwtService.generateAccessToken(any(CustomUserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken("test@email.com")).thenReturn("refreshToken");
        when(jwtService.getJwtExpirationMs()).thenReturn(3600000L);
        when(authMapper.toUserDetailsDto(any(CustomUserDetails.class))).thenReturn(testUserDetailsDto);

        // When
        AuthResponse result = authService.processPasswordReset(resetToken, newPassword);

        // Then
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        verify(jwtService).validatePasswordResetToken(resetToken);
        verify(passwordEncoder).encode(newPassword);
        verify(accountRepository).save(argThat(account -> 
            account.getPasswordResetToken() == null &&
            account.getPasswordResetExpiry() == null &&
            "encodedNewPassword".equals(account.getPassword())));
    }

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_InvalidToken_ThrowsAccessDeniedException() {
        // Given
        String invalidToken = "invalidToken";
        when(jwtService.validatePasswordResetToken(invalidToken)).thenReturn(false);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.processPasswordReset(invalidToken, "newPassword"));
        assertEquals("Invalid token", exception.getMessage());
        verify(jwtService).validatePasswordResetToken(invalidToken);
        verify(accountRepository, never()).findByEmail(any());
    }

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_TokenMismatch_ThrowsAccessDeniedException() {
        // Given
        String resetToken = "validToken";
        testAccount.setPasswordResetToken("differentToken");
        testAccount.setPasswordResetExpiry(Instant.now().plus(10, ChronoUnit.MINUTES));

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.getEmailFromPasswordResetToken(resetToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.processPasswordReset(resetToken, "newPassword"));
        assertEquals("Token mismatch", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_ExpiredToken_ThrowsAccessDeniedException() {
        // Given
        String resetToken = "validToken";
        testAccount.setPasswordResetToken(resetToken);
        testAccount.setPasswordResetExpiry(Instant.now().minus(10, ChronoUnit.MINUTES)); // Expired

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.getEmailFromPasswordResetToken(resetToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> authService.processPasswordReset(resetToken, "newPassword"));
        assertEquals("Token expired", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_AccountNotFound_ThrowsRuntimeException() {
        // Given
        String resetToken = "validToken";
        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.getEmailFromPasswordResetToken(resetToken)).thenReturn("nonexistent@email.com");
        when(accountRepository.findByEmail("nonexistent@email.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.processPasswordReset(resetToken, "newPassword"));
        assertEquals("Account not found with email: nonexistent@email.com", exception.getMessage());
        verify(accountRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @Tag("processPasswordReset")
    void testProcessPasswordReset_PasswordEncodingException_PropagatesException() {
        // Given
        String resetToken = "validToken";
        testAccount.setPasswordResetToken(resetToken);
        testAccount.setPasswordResetExpiry(Instant.now().plus(10, ChronoUnit.MINUTES));

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.getEmailFromPasswordResetToken(resetToken)).thenReturn("test@email.com");
        when(accountRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.encode("newPassword")).thenThrow(new RuntimeException("Encoding failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.processPasswordReset(resetToken, "newPassword"));
        assertEquals("Encoding failed", exception.getMessage());
        verify(passwordEncoder).encode("newPassword");
        verify(accountRepository, never()).save(any());
    }

    // ================ VALIDATE PASSWORD RESET TOKEN TESTS ================

    @Test
    @Tag("validatePasswordResetToken")
    void testValidatePasswordResetToken_ValidToken_ReturnsTrue() {
        // Given
        when(jwtService.validatePasswordResetToken("validToken")).thenReturn(true);

        // When
        boolean result = authService.validatePasswordResetToken("validToken");

        // Then
        assertTrue(result);
        verify(jwtService).validatePasswordResetToken("validToken");
    }

    @Test
    @Tag("validatePasswordResetToken")
    void testValidatePasswordResetToken_InvalidToken_ReturnsFalse() {
        // Given
        when(jwtService.validatePasswordResetToken("invalidToken")).thenReturn(false);

        // When
        boolean result = authService.validatePasswordResetToken("invalidToken");

        // Then
        assertFalse(result);
        verify(jwtService).validatePasswordResetToken("invalidToken");
    }

    @Test
    @Tag("validatePasswordResetToken")
    void testValidatePasswordResetToken_NullToken_ReturnsFalse() {
        // Given
        when(jwtService.validatePasswordResetToken(null)).thenReturn(false);

        // When
        boolean result = authService.validatePasswordResetToken(null);

        // Then
        assertFalse(result);
        verify(jwtService).validatePasswordResetToken(null);
    }

    @Test
    @Tag("validatePasswordResetToken")
    void testValidatePasswordResetToken_EmptyToken_ReturnsFalse() {
        // Given
        when(jwtService.validatePasswordResetToken("")).thenReturn(false);

        // When
        boolean result = authService.validatePasswordResetToken("");

        // Then
        assertFalse(result);
        verify(jwtService).validatePasswordResetToken("");
    }

    @Test
    @Tag("validatePasswordResetToken")
    void testValidatePasswordResetToken_JwtServiceException_PropagatesException() {
        // Given
        when(jwtService.validatePasswordResetToken("token")).thenThrow(new RuntimeException("Validation failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.validatePasswordResetToken("token"));
        assertEquals("Validation failed", exception.getMessage());
        verify(jwtService).validatePasswordResetToken("token");
    }

    // ================ VALIDATE EMAIL TESTS ================

    @Test
    @Tag("validateEmail")
    void testValidateEmail_EmailExists_ReturnsTrue() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("test@email.com")).thenReturn(true);

        // When
        boolean result = authService.validateEmail("test@email.com");

        // Then
        assertTrue(result);
        verify(accountRepository).existsByEmailAndNotDeleted("test@email.com");
    }

    @Test
    @Tag("validateEmail")
    void testValidateEmail_EmailNotExists_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("nonexistent@email.com")).thenReturn(false);

        // When
        boolean result = authService.validateEmail("nonexistent@email.com");

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted("nonexistent@email.com");
    }

    @Test
    @Tag("validateEmail")
    void testValidateEmail_NullEmail_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted(null)).thenReturn(false);

        // When
        boolean result = authService.validateEmail(null);

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted(null);
    }

    @Test
    @Tag("validateEmail")
    void testValidateEmail_EmptyEmail_ReturnsFalse() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("")).thenReturn(false);

        // When
        boolean result = authService.validateEmail("");

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted("");
    }

    @Test
    @Tag("validateEmail")
    void testValidateEmail_InvalidEmailFormat_HandledByRepository() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("invalid-email")).thenReturn(false);

        // When
        boolean result = authService.validateEmail("invalid-email");

        // Then
        assertFalse(result);
        verify(accountRepository).existsByEmailAndNotDeleted("invalid-email");
    }

    @Test
    @Tag("validateEmail")
    void testValidateEmail_RepositoryException_PropagatesException() {
        // Given
        when(accountRepository.existsByEmailAndNotDeleted("test@email.com"))
            .thenThrow(new RuntimeException("Database query failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.validateEmail("test@email.com"));
        assertEquals("Database query failed", exception.getMessage());
        verify(accountRepository).existsByEmailAndNotDeleted("test@email.com");
    }
}