# Flight Management System - Testing Framework Guide

## Overview
This document serves as a comprehensive guide for testing the Flight Management System, including framework configuration, testing methodologies, and execution procedures. Use this as a reference for all testing-related tasks and debugging activities.

## System Architecture
- **Backend**: Spring Boot 3.4.4 with Java 17 (Port 8080)
- **Frontend**: React 19 with TypeScript and Vite (Port 3000)
- **Database**: MySQL with JPA/Hibernate
- **API Communication**: REST API with Axios client

## Frontend Testing Stack

### Core Technologies
- **Primary Framework**: Vitest (modern Jest alternative)
- **Testing Library**: React Testing Library with Jest DOM matchers
- **Environment**: jsdom for browser-like testing environment
- **Language**: TypeScript with ts-jest transformer
- **UI Testing**: @vitest/ui for interactive test runner
- **Coverage**: @vitest/coverage-v8 provider

### Configuration Files
```
frontend/
├── jest.config.js          # Jest configuration (legacy, use vitest)
├── vite.config.ts          # Vite + Vitest configuration
├── src/setupTests.ts       # Global test setup
└── src/test/setup.ts       # Additional test utilities
```

### Test File Structure
```
src/
├── components/
│   └── **/__tests__/*.test.tsx     # Component tests
├── services/
│   └── __tests__/*.test.ts         # Service layer tests
├── hooks/
│   └── __tests__/*.test.tsx        # Custom hooks tests
└── models/                         # Type definitions
```

### Frontend Test Commands
```powershell
# Navigate to frontend directory
cd "d:\flight-management-system\frontend"

# Run tests in watch mode
npm test

# Run tests once
npm run test:run

# Interactive test UI
npm run test:ui

# Generate coverage report
npm run coverage
```

### Frontend Testing Patterns

#### Service Testing Example
```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { flightService } from '../flightService'
import { apiClient } from '../api'

vi.mock('../api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('FlightService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })
  
  it('should search flights with correct parameters', async () => {
    // Test implementation
  })
})
```

#### Component Testing Example
```typescript
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FlightSearch from '../FlightSearch';

describe('FlightSearch', () => {
  it('should render search form', async () => {
    render(<FlightSearch />);
    expect(screen.getByText('Search Flights')).toBeInTheDocument();
  })
})
```

### Frontend Test Setup Configuration
```typescript
// src/setupTests.ts - Global mocks and setup
import '@testing-library/jest-dom';

// Mock localStorage, sessionStorage, window.matchMedia, IntersectionObserver
// Clean up after each test with afterEach()
```

## Backend Testing Stack

### Core Technologies
- **Primary Framework**: Spring Boot Test with JUnit 5
- **Mocking**: Mockito for service layer mocking
- **Web Testing**: MockMvc for REST API testing
- **Database Testing**: @DataJpaTest for repository layer
- **Integration Testing**: @SpringBootTest for full context

### Test Categories

#### 1. Unit Tests (`@WebMvcTest`)
```java
@WebMvcTest(FlightController.class)
class FlightControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private FlightService flightService;
    
    @Test
    void searchFlights_ShouldReturnFlights() throws Exception {
        // Test implementation
    }
}
```

#### 2. Integration Tests (`@SpringBootTest`)
```java
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class TicketBookingIntegrationTest {
    @Autowired private MockMvc mockMvc;
    
    @Test
    void bookTickets_WithValidData_ShouldCreateBooking() throws Exception {
        // Test implementation
    }
}
```

#### 3. Repository Tests (`@DataJpaTest`)
```java
@DataJpaTest
class FlightRepositoryTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private FlightRepository flightRepository;
    
    @Test
    void findByDepartureAndArrival_ShouldReturnFlights() {
        // Test implementation
    }
}
```

### Backend Test Commands
```powershell
# Navigate to backend directory
cd "d:\flight-management-system\backend"

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FlightControllerTest

# Run integration tests only
mvn test -Dtest=*Integration*

# Run with specific profile
mvn test -Dspring.profiles.active=test

# Generate test reports
mvn surefire-report:report
```

## Testing Methodology

### Frontend Testing Approach
1. **AAA Pattern**: Arrange, Act, Assert structure
2. **User-Centric Testing**: Focus on user interactions over implementation
3. **Mock External Dependencies**: API calls, localStorage, browser APIs
4. **Component Isolation**: Test components with mocked dependencies
5. **Accessibility Testing**: Include screen reader and keyboard navigation tests

### Backend Testing Approach
1. **Layer Testing**: Separate testing for each application layer
2. **Test Data Management**: Use @Transactional for database rollback
3. **Mock vs Real**: MockMvc for web layer, real database for integration
4. **Security Testing**: Authentication and authorization validation
5. **Error Handling**: Test exception scenarios and error responses

## Application Startup for Testing

### Starting Both Applications
```powershell
# Terminal 1 - Backend
cd "d:\flight-management-system\backend"
mvn spring-boot:run

# Terminal 2 - Frontend  
cd "d:\flight-management-system\frontend"
npm run dev

# Verify applications are running
# Backend: http://localhost:8080/api/airports
# Frontend: http://localhost:3000
```

### Test Database Setup
- Use `@ActiveProfiles("test")` for test-specific configuration
- Configure test database in `application-test.properties`
- Use `@Transactional` for automatic rollback after tests

## API Testing

### Manual API Testing
```powershell
# Test backend endpoints directly
curl -X GET "http://localhost:8080/api/airports" -H "accept: application/json"

# Test flight search
curl -X GET "http://localhost:8080/api/flights/search?departureAirportId=1&arrivalAirportId=2&departureTime=2025-05-28T10:00:00" -H "accept: application/json"

# Test with PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/airports" -UseBasicParsing | ConvertFrom-Json
```

### Frontend-Backend Integration Testing
1. Start both applications
2. Open browser to http://localhost:3000
3. Test user workflows through UI
4. Monitor network tab for API calls
5. Check browser console for errors

## Common Testing Scenarios

### Flight Search Testing
1. **Valid Search**: Test with valid airport IDs and future dates
2. **Invalid Parameters**: Test with missing or invalid data
3. **No Results**: Test search that returns empty results
4. **Error Handling**: Test network failures and server errors

### User Authentication Testing
1. **Valid Login**: Test with correct credentials
2. **Invalid Credentials**: Test login failures
3. **Session Management**: Test token expiration and refresh
4. **Protected Routes**: Test unauthorized access attempts

### Booking Flow Testing
1. **Complete Booking**: End-to-end booking with valid data
2. **Validation Errors**: Test form validation and error display
3. **Payment Processing**: Test payment integration (if implemented)
4. **Confirmation**: Test booking confirmation and ticket generation

## Debugging Guide

### Frontend Debugging
```powershell
# Check for compilation errors
npm run build

# Run linting
npm run lint

# Start with debug logging
npm run dev -- --debug

# Check test failures
npm run test:run -- --verbose
```

### Backend Debugging
```powershell
# Check for compilation errors
mvn compile

# Run with debug profile
mvn spring-boot:run -Dspring.profiles.active=debug

# Check application logs
mvn spring-boot:run -Dlogging.level.com.flightmanagement=DEBUG

# Verify database connectivity
mvn test -Dtest=*Repository*
```

## Performance Testing

### Frontend Performance
- Use Chrome DevTools for performance profiling
- Test component rendering performance
- Monitor bundle size and loading times
- Test with throttled network conditions

### Backend Performance
- Use Spring Boot Actuator for metrics
- Test API response times under load
- Monitor database query performance
- Use profiling tools for memory usage

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Flight Management System Tests

on: [push, pull_request]

jobs:
  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: cd frontend && npm install
      - run: cd frontend && npm run test:run
      
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
      - run: cd backend && mvn test
```

## Best Practices

### General Testing Guidelines
1. **Test Naming**: Use descriptive test names that explain the scenario
2. **Test Independence**: Each test should run independently
3. **Data Cleanup**: Always clean up test data after tests
4. **Mocking Strategy**: Mock external dependencies, not internal logic
5. **Coverage Goals**: Aim for 80%+ code coverage, 100% critical path coverage

### Error Handling Testing
1. **Network Failures**: Test API timeout and connection errors
2. **Validation Errors**: Test all form validation scenarios
3. **Authentication Errors**: Test expired tokens and unauthorized access
4. **Database Errors**: Test constraint violations and connection issues

### Security Testing
1. **Input Validation**: Test SQL injection and XSS prevention
2. **Authentication**: Test login, logout, and session management
3. **Authorization**: Test role-based access control
4. **CORS**: Test cross-origin request handling

## Troubleshooting Common Issues

### Frontend Issues
- **Port conflicts**: Check if port 3000 is already in use
- **API connection**: Verify proxy configuration in vite.config.ts
- **Mock failures**: Ensure all external dependencies are properly mocked
- **Type errors**: Check TypeScript configuration and type definitions

### Backend Issues
- **Database connection**: Verify MySQL is running and accessible
- **Port conflicts**: Check if port 8080 is already in use
- **Dependency injection**: Verify Spring bean configuration
- **Test profile**: Ensure test profile is properly configured

### Integration Issues
- **CORS errors**: Check backend CORS configuration
- **API path mismatches**: Verify frontend API client configuration
- **Authentication**: Check JWT token handling and validation
- **Data format**: Verify request/response data formats match

This guide should be referenced for all testing activities and can serve as a comprehensive system prompt for debugging and testing tasks in the Flight Management System.
