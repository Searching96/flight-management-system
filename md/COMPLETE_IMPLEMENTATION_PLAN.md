# Flight Management System - Complete Implementation Plan

## Executive Summary

The Flight Management System currently has a **fully functional backend** with all core features implemented, but **security is disabled for demo purposes**. The frontend has most features implemented but lacks complete integration with security and some advanced features. This plan outlines the steps to complete both systems with production-ready security and peripherals.

## Current State Analysis

### ✅ Backend - Fully Implemented Core Features
- **14 complete entities** with all CRUD operations
- **Complete business logic** for flight management, booking, and payments
- **REST APIs** for all operations
- **Security framework** in place but **disabled** (permitAll() configuration)
- **JWT infrastructure** partially implemented but not active

### 🚧 Frontend - Core Features Complete, Security Integration Needed
- **Authentication UI** implemented but not fully integrated with backend security
- **All major components** for flight search, booking, admin panels
- **Role-based access** framework exists but not enforced
- **API calls** ready but missing proper authentication headers

### ❌ Missing Components
1. **Production Security Configuration**
2. **JWT Authentication Filter**
3. **Role-based API Authorization**
4. **Email Notification Service**
5. **Advanced Admin Features**
6. **Production Error Handling**

---

## Phase 1: Security Implementation (Priority 1)

### 1.1 Backend Security Configuration

#### Required Files to Create/Modify:

**1. JWT Utility Service**
```java
// File: backend/src/main/java/com/flightmanagement/security/JwtUtil.java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    /**
     * Generate JWT token for authenticated user
     * @param account User account details
     * @return JWT token string
     */
    public String generateToken(Account account);
    
    /**
     * Extract username from JWT token
     * @param token JWT token
     * @return username
     */
    public String getUsernameFromToken(String token);
    
    /**
     * Validate JWT token
     * @param token JWT token
     * @return true if valid
     */
    public Boolean validateToken(String token);
    
    /**
     * Extract account type from token for role validation
     * @param token JWT token
     * @return account type (1=customer, 2=employee)
     */
    public Integer getAccountTypeFromToken(String token);
}
```

**API Endpoints:**
- No new endpoints needed - utility class

**2. JWT Authentication Filter**
```java
// File: backend/src/main/java/com/flightmanagement/security/JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    /**
     * Process incoming requests and validate JWT tokens
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain);
    
    /**
     * Extract Bearer token from Authorization header
     * @param request HTTP request
     * @return JWT token or null
     */
    private String extractTokenFromRequest(HttpServletRequest request);
}
```

**3. User Details Service**
```java
// File: backend/src/main/java/com/flightmanagement/security/CustomUserDetailsService.java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    /**
     * Load user by email for authentication
     * @param email User email
     * @return UserDetails for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email);
    
    /**
     * Convert Account entity to UserDetails
     * @param account Account entity
     * @return UserDetails
     */
    private UserDetails createUserDetails(Account account);
}
```

**4. Updated Security Configuration**
```java
// File: backend/src/main/java/com/flightmanagement/config/SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    /**
     * Configure security filter chain with JWT authentication
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http);
    
    /**
     * Authentication manager for login processing
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config);
}
```

**Endpoint Security Rules:**
```java
// Public endpoints (no authentication required)
.requestMatchers("/api/accounts/login", "/api/accounts/register").permitAll()
.requestMatchers("/api/flights/search", "/api/airports/search").permitAll()
.requestMatchers("/actuator/health").permitAll()

// Customer endpoints (account_type = 1)
.requestMatchers(HttpMethod.POST, "/api/tickets/book").hasRole("CUSTOMER")
.requestMatchers("/api/tickets/my-tickets").hasRole("CUSTOMER")
.requestMatchers("/api/chatboxes/customer/**").hasRole("CUSTOMER")

// Employee endpoints (account_type = 2)
.requestMatchers("/api/employees/**").hasRole("EMPLOYEE")
.requestMatchers("/api/parameters/**").hasRole("EMPLOYEE")
.requestMatchers("/api/flights/manage/**").hasRole("EMPLOYEE")

// Admin endpoints (employee_type = 5)
.requestMatchers("/api/accounts/admin/**").hasAuthority("ADMIN")
.requestMatchers("/api/system/**").hasAuthority("ADMIN")
```

**5. Updated Account Service with JWT**
```java
// File: backend/src/main/java/com/flightmanagement/service/impl/AccountServiceImpl.java
@Service
public class AccountServiceImpl implements AccountService {
    
    /**
     * Authenticate user and generate JWT token
     * @param email User email
     * @param password User password
     * @return LoginResponseDto with JWT token
     */
    @Override
    public LoginResponseDto login(String email, String password);
    
    /**
     * Validate JWT token and refresh if needed
     * @param token Current JWT token
     * @return New LoginResponseDto with refreshed token
     */
    @Override
    public LoginResponseDto refreshToken(String token);
}
```

**API Endpoints:**
- `POST /api/accounts/login` - Returns JWT token
- `POST /api/accounts/refresh` - Refresh JWT token
- `POST /api/accounts/logout` - Invalidate token (optional)

### 1.2 Frontend Security Integration

**1. API Client with JWT Authentication**
```typescript
// File: frontend/src/services/api.ts
export class ApiClient {
    
    /**
     * Configure request interceptor to add JWT token
     */
    setupInterceptors(): void;
    
    /**
     * Handle 401 responses and redirect to login
     */
    setupResponseInterceptor(): void;
    
    /**
     * Get stored JWT token
     * @returns JWT token or null
     */
    private getAuthToken(): string | null;
}
```

**2. Enhanced Authentication Hook**
```typescript
// File: frontend/src/hooks/useAuth.tsx
interface AuthContextType {
    user: Account | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    refreshToken: () => Promise<void>;
    isAuthenticated: boolean;
    hasRole: (role: 'customer' | 'employee' | 'admin') => boolean;
    loading: boolean;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    
    /**
     * Login with JWT token handling
     * @param email User email
     * @param password User password
     */
    const login = async (email: string, password: string): Promise<void>;
    
    /**
     * Logout and clear tokens
     */
    const logout = (): void;
    
    /**
     * Check if user has specific role
     * @param role Role to check
     * @returns True if user has role
     */
    const hasRole = (role: 'customer' | 'employee' | 'admin'): boolean;
    
    /**
     * Refresh JWT token automatically
     */
    const refreshToken = async (): Promise<void>;
};
```

**3. Role-Based Route Protection**
```typescript
// File: frontend/src/components/routes/ProtectedRoute.tsx
interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredRole?: 'customer' | 'employee' | 'admin';
    requiredPermission?: string;
}

/**
 * Protect routes based on authentication and roles
 * @param children Child components
 * @param requiredRole Required role for access
 * @param requiredPermission Specific permission required
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps>;
```

---

## Phase 2: Role-Based Access Control (Priority 2)

### 2.1 Backend Role Implementation

**1. Role-Based Service Methods**
```java
// File: backend/src/main/java/com/flightmanagement/service/impl/TicketServiceImpl.java
@Service
@PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE')")
public class TicketServiceImpl implements TicketService {
    
    /**
     * Book tickets for authenticated customer
     * @param bookingDto Booking details
     * @param principal Authenticated user
     * @return List of created tickets
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TicketDto> bookTickets(BookingDto bookingDto, Principal principal);
    
    /**
     * Get customer's tickets
     * @param customerId Customer ID
     * @param principal Authenticated user
     * @return List of customer tickets
     */
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.name")
    public List<TicketDto> getCustomerTickets(Integer customerId, Principal principal);
    
    /**
     * Cancel ticket (customer or employee)
     * @param ticketId Ticket ID
     * @param principal Authenticated user
     */
    @PreAuthorize("hasRole('EMPLOYEE') or (@ticketService.isTicketOwner(#ticketId, authentication.name))")
    public void cancelTicket(Integer ticketId, Principal principal);
}
```

**API Endpoints with Security:**
- `POST /api/tickets/book` - Customer only
- `GET /api/tickets/customer/{customerId}` - Own tickets only
- `PUT /api/tickets/{ticketId}/cancel` - Owner or employee
- `GET /api/tickets/all` - Employees only

**2. Employee Type-Based Authorization**
```java
// File: backend/src/main/java/com/flightmanagement/service/impl/FlightServiceImpl.java
@Service
public class FlightServiceImpl implements FlightService {
    
    /**
     * Create new flight (flight operations employees only)
     * @param flightDto Flight details
     * @param principal Authenticated employee
     * @return Created flight
     */
    @PreAuthorize("hasRole('EMPLOYEE') and @employeeService.hasEmployeeType(authentication.name, 1)")
    public FlightDto createFlight(FlightDto flightDto, Principal principal);
    
    /**
     * Update flight schedule (flight operations only)
     * @param flightId Flight ID
     * @param flightDto Updated flight details
     * @param principal Authenticated employee
     * @return Updated flight
     */
    @PreAuthorize("hasRole('EMPLOYEE') and @employeeService.hasEmployeeType(authentication.name, 1)")
    public FlightDto updateFlight(Integer flightId, FlightDto flightDto, Principal principal);
}
```

**Employee Types and Permissions:**
1. **Flight Operations (Type 1)**: Flight scheduling, aircraft management
2. **Booking/Ticketing (Type 2)**: Ticket management, customer bookings
3. **Customer Service (Type 3)**: Chat management, customer support
4. **Accounting (Type 4)**: Financial reports, payment management
5. **System Admin (Type 5)**: User management, system configuration

### 2.2 Frontend Role-Based UI

**1. Permission-Based Components**
```typescript
// File: frontend/src/hooks/usePermissions.tsx
export const usePermissions = () => {
    const { user } = useAuth();
    
    return {
        // Customer permissions
        canBookTickets: user?.accountType === 1,
        canViewOwnTickets: user?.accountType === 1,
        canUseChat: user?.accountType === 1,
        
        // Employee permissions
        canManageFlights: user?.accountType === 2 && hasEmployeeType(1),
        canManageBookings: user?.accountType === 2 && hasEmployeeType(2),
        canManageCustomerService: user?.accountType === 2 && hasEmployeeType(3),
        canViewReports: user?.accountType === 2 && hasEmployeeType(4),
        canManageSystem: user?.accountType === 2 && hasEmployeeType(5),
        
        // General employee permissions
        canAccessAdminPanel: user?.accountType === 2,
        canViewAllTickets: user?.accountType === 2
    };
};
```

**2. Conditional Navigation**
```typescript
// File: frontend/src/components/layout/Header.tsx
const Header: React.FC = () => {
    const { user, logout } = useAuth();
    const permissions = usePermissions();
    
    return (
        <nav>
            {/* Public navigation */}
            <Link to="/flights">Search Flights</Link>
            
            {/* Customer navigation */}
            {permissions.canBookTickets && (
                <Link to="/my-bookings">My Bookings</Link>
            )}
            
            {/* Employee navigation */}
            {permissions.canAccessAdminPanel && (
                <Link to="/admin">Admin Panel</Link>
            )}
            
            {/* System admin navigation */}
            {permissions.canManageSystem && (
                <Link to="/system-settings">System Settings</Link>
            )}
        </nav>
    );
};
```

---

## Phase 3: Enhanced Features (Priority 3)

### 3.1 Email Notification Service

**1. Backend Email Service**
```java
// File: backend/src/main/java/com/flightmanagement/service/EmailService.java
@Service
public class EmailService {
    
    /**
     * Send booking confirmation email
     * @param ticket Booked ticket details
     * @param customerEmail Customer email
     */
    public void sendBookingConfirmation(TicketDto ticket, String customerEmail);
    
    /**
     * Send flight change notification
     * @param flightId Flight ID
     * @param passengers List of affected passengers
     */
    public void sendFlightChangeNotification(Integer flightId, List<PassengerDto> passengers);
    
    /**
     * Send payment reminder
     * @param ticketId Unpaid ticket ID
     * @param customerEmail Customer email
     */
    public void sendPaymentReminder(Integer ticketId, String customerEmail);
    
    /**
     * Send cancellation confirmation
     * @param ticketId Cancelled ticket ID
     * @param customerEmail Customer email
     */
    public void sendCancellationConfirmation(Integer ticketId, String customerEmail);
}
```

**Dependencies to Add:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**Configuration:**
```properties
# application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**2. Notification Service Integration**
```java
// File: backend/src/main/java/com/flightmanagement/service/NotificationService.java
@Service
public class NotificationService {
    
    /**
     * Send notification to customer
     * @param customerId Customer ID
     * @param type Notification type
     * @param message Notification message
     */
    public void sendNotification(Integer customerId, NotificationType type, String message);
    
    /**
     * Get customer notifications
     * @param customerId Customer ID
     * @return List of notifications
     */
    public List<NotificationDto> getCustomerNotifications(Integer customerId);
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     */
    public void markAsRead(Integer notificationId);
}
```

**New Entity and Database Table:**
```sql
-- Add to fms_db.sql
CREATE TABLE IF NOT EXISTS notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
```

### 3.2 Advanced Admin Features

**1. System Reports Service**
```java
// File: backend/src/main/java/com/flightmanagement/service/ReportService.java
@Service
@PreAuthorize("hasRole('EMPLOYEE')")
public class ReportService {
    
    /**
     * Generate revenue report by date range
     * @param startDate Start date
     * @param endDate End date
     * @return Revenue report data
     */
    @PreAuthorize("@employeeService.hasEmployeeType(authentication.name, 4)")
    public RevenueReportDto generateRevenueReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate flight utilization report
     * @param flightId Flight ID (optional)
     * @return Flight utilization data
     */
    @PreAuthorize("@employeeService.hasEmployeeType(authentication.name, 1)")
    public FlightUtilizationReportDto generateFlightUtilizationReport(Integer flightId);
    
    /**
     * Generate customer analytics report
     * @return Customer analytics data
     */
    @PreAuthorize("@employeeService.hasEmployeeType(authentication.name, 3)")
    public CustomerAnalyticsReportDto generateCustomerAnalyticsReport();
}
```

**API Endpoints:**
- `GET /api/reports/revenue?startDate={date}&endDate={date}` - Revenue report
- `GET /api/reports/flight-utilization?flightId={id}` - Flight utilization
- `GET /api/reports/customer-analytics` - Customer analytics

**2. Frontend Admin Dashboard**
```typescript
// File: frontend/src/components/admin/ReportsPanel.tsx
interface ReportsPanel {
    /**
     * Display revenue charts and statistics
     */
    showRevenueReport(): JSX.Element;
    
    /**
     * Display flight utilization metrics
     */
    showFlightUtilization(): JSX.Element;
    
    /**
     * Display customer analytics
     */
    showCustomerAnalytics(): JSX.Element;
}
```

### 3.3 Real-time Chat Enhancement

**1. WebSocket Configuration**
```java
// File: backend/src/main/java/com/flightmanagement/config/WebSocketConfig.java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    /**
     * Configure WebSocket handlers for real-time chat
     * @param registry WebSocket handler registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry);
}
```

**2. Chat Handler**
```java
// File: backend/src/main/java/com/flightmanagement/websocket/ChatWebSocketHandler.java
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    /**
     * Handle incoming chat messages
     * @param session WebSocket session
     * @param message Text message
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message);
    
    /**
     * Handle client connection
     * @param session WebSocket session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session);
}
```

**3. Frontend WebSocket Integration**
```typescript
// File: frontend/src/hooks/useWebSocket.tsx
export const useWebSocket = (chatboxId: number) => {
    
    /**
     * Connect to WebSocket for real-time chat
     * @param chatboxId Chat session ID
     */
    const connect = (chatboxId: number): void;
    
    /**
     * Send message through WebSocket
     * @param message Message content
     */
    const sendMessage = (message: string): void;
    
    /**
     * Disconnect from WebSocket
     */
    const disconnect = (): void;
};
```

---

## Phase 4: Production Readiness (Priority 4)

### 4.1 Enhanced Error Handling

**1. Global Exception Handler**
```java
// File: backend/src/main/java/com/flightmanagement/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle authentication failures
     * @param ex Authentication exception
     * @return Error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex);
    
    /**
     * Handle authorization failures
     * @param ex Access denied exception
     * @return Error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex);
    
    /**
     * Handle validation errors
     * @param ex Validation exception
     * @return Error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex);
    
    /**
     * Handle business logic errors
     * @param ex Runtime exception
     * @return Error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex);
}
```

**2. Frontend Error Boundary**
```typescript
// File: frontend/src/components/error/ErrorBoundary.tsx
interface ErrorBoundaryState {
    hasError: boolean;
    error: Error | null;
}

export class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
    
    /**
     * Catch JavaScript errors in component tree
     * @param error Caught error
     * @param errorInfo Error information
     */
    componentDidCatch(error: Error, errorInfo: React.ErrorInfo): void;
    
    /**
     * Render error UI
     * @returns Error display component
     */
    render(): React.ReactNode;
}
```

### 4.2 Input Validation and Sanitization

**1. Backend Validation**
```java
// File: backend/src/main/java/com/flightmanagement/dto/BookingDto.java
public class BookingDto {
    
    @NotNull(message = "Flight ID is required")
    @Positive(message = "Flight ID must be positive")
    private Integer flightId;
    
    @NotNull(message = "Ticket class ID is required")
    @Positive(message = "Ticket class ID must be positive")
    private Integer ticketClassId;
    
    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<@Valid PassengerDto> passengers;
    
    @Email(message = "Customer email must be valid")
    private String customerEmail;
    
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "Booking reference must be 6 alphanumeric characters")
    private String bookingReference;
}
```

**2. Frontend Form Validation**
```typescript
// File: frontend/src/utils/validation.ts
export const ValidationRules = {
    
    /**
     * Email validation pattern
     */
    email: {
        required: 'Email is required',
        pattern: {
            value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            message: 'Please enter a valid email address'
        }
    },
    
    /**
     * Password validation rules
     */
    password: {
        required: 'Password is required',
        minLength: {
            value: 8,
            message: 'Password must be at least 8 characters'
        },
        pattern: {
            value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
            message: 'Password must contain uppercase, lowercase, number, and special character'
        }
    },
    
    /**
     * Phone number validation
     */
    phoneNumber: {
        required: 'Phone number is required',
        pattern: {
            value: /^\+?[\d\s-()]+$/,
            message: 'Please enter a valid phone number'
        }
    }
};
```

### 4.3 Logging and Monitoring

**1. Application Logging**
```java
// File: backend/src/main/java/com/flightmanagement/service/impl/TicketServiceImpl.java
@Service
@Slf4j
public class TicketServiceImpl implements TicketService {
    
    @Override
    public List<TicketDto> bookTickets(BookingDto bookingDto, Principal principal) {
        log.info("Processing booking request for customer: {} flight: {}", 
                principal.getName(), bookingDto.getFlightId());
        
        try {
            // Booking logic
            List<TicketDto> tickets = processBooking(bookingDto);
            
            log.info("Successfully booked {} tickets for customer: {}", 
                    tickets.size(), principal.getName());
            
            return tickets;
        } catch (Exception e) {
            log.error("Failed to process booking for customer: {} - Error: {}", 
                     principal.getName(), e.getMessage(), e);
            throw e;
        }
    }
}
```

**2. Security Event Logging**
```java
// File: backend/src/main/java/com/flightmanagement/security/SecurityEventLogger.java
@Component
@Slf4j
public class SecurityEventLogger {
    
    /**
     * Log successful login
     * @param email User email
     * @param ipAddress Client IP
     */
    public void logSuccessfulLogin(String email, String ipAddress);
    
    /**
     * Log failed login attempt
     * @param email Attempted email
     * @param ipAddress Client IP
     */
    public void logFailedLogin(String email, String ipAddress);
    
    /**
     * Log unauthorized access attempt
     * @param email User email
     * @param endpoint Attempted endpoint
     */
    public void logUnauthorizedAccess(String email, String endpoint);
}
```

---

## Implementation Timeline

### Week 1-2: Security Foundation
1. Implement JWT utility and authentication filter
2. Update security configuration
3. Integrate JWT tokens in frontend
4. Test authentication flow

### Week 3-4: Role-Based Access Control
1. Implement role-based service security
2. Add permission checks in frontend
3. Create role-based navigation
4. Test authorization scenarios

### Week 5-6: Enhanced Features
1. Implement email notification service
2. Add notification system
3. Create advanced admin features
4. Implement real-time chat

### Week 7-8: Production Readiness
1. Implement comprehensive error handling
2. Add input validation and sanitization
3. Set up logging and monitoring
4. Performance testing and optimization

## Testing Strategy

### 1. Security Testing
- **Authentication tests**: Valid/invalid credentials
- **Authorization tests**: Role-based access control
- **JWT tests**: Token validation, expiration, refresh

### 2. Integration Testing
- **End-to-end booking flow**: Search → Book → Pay → Confirm
- **Role-based workflows**: Customer vs Employee vs Admin
- **Error handling**: Network failures, validation errors

### 3. Performance Testing
- **Load testing**: Concurrent user scenarios
- **API performance**: Response time benchmarks
- **Database optimization**: Query performance

## Security Considerations

### 1. Data Protection
- **JWT secrets**: Use strong, environment-specific secrets
- **Password encryption**: BCrypt with appropriate rounds
- **Sensitive data**: Never log passwords or tokens

### 2. API Security
- **Rate limiting**: Prevent brute force attacks
- **Input sanitization**: Prevent SQL injection, XSS
- **CORS configuration**: Restrict allowed origins

### 3. Frontend Security
- **Token storage**: Consider httpOnly cookies vs localStorage
- **XSS prevention**: Sanitize user inputs
- **CSRF protection**: Implement CSRF tokens if using cookies

This plan provides a complete roadmap to transform the current demo system into a production-ready flight management platform with robust security, role-based access control, and enhanced features.
