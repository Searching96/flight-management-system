# Flight Management System - Task Assignment for Software Testing Project

**Team Members:**
- Nguyễn Phúc Thịnh (Member 1)
- Trần Xuân Thịnh (Member 2)  
- Trần Đức Thịnh (Member 3)

**Project Scope:** Rebuild the Flight Management System from scratch with comprehensive testing implementation using JUnit and modern Java testing frameworks.

---

## Project Overview

The Flight Management System is a full-stack application consisting of:
- **Backend**: Spring Boot 3.4.4 with Java 17, JPA/Hibernate, MySQL
- **Frontend**: React 19 with TypeScript, Vite, Bootstrap
- **Key Features**: Flight booking, customer management, employee portal, chat system, statistics

---

## Technology Stack for Testing

### Backend Testing Technologies
- **JUnit 5**: Core testing framework
- **Mockito**: Mocking framework for unit tests
- **TestContainers**: Integration testing with real database
- **Spring Boot Test**: Integration testing with Spring context
- **WireMock**: API mocking for external services
- **AssertJ**: Fluent assertions
- **Jacoco**: Code coverage analysis

### Frontend Testing Technologies
- **Vitest**: Modern testing framework (already configured)
- **Testing Library**: React component testing
- **Jest DOM**: Additional assertions
- **MSW (Mock Service Worker)**: API mocking

---

## Task Division

## **Member 1: Nguyễn Phúc Thịnh - Backend Core & Testing Infrastructure**

### Primary Responsibilities
1. **Backend Foundation Setup (Week 1-2)**
   - Set up Spring Boot project structure
   - Configure Maven with testing dependencies
   - Database schema design and JPA entities
   - Spring Security configuration with JWT
   - Basic error handling and validation

2. **Core Domain Testing (Week 3-4)**
   - **Entity Layer Testing**
     - Unit tests for all JPA entities (Account, Flight, Ticket, etc.)
     - Validation annotation testing
     - Entity relationship testing
   - **Repository Layer Testing**
     - JPA repository method testing with @DataJpaTest
     - Custom query testing
     - Database constraint testing with TestContainers

3. **Service Layer Testing (Week 5-6)**
   - **Unit Testing with Mockito**
     - FlightService, AccountService, TicketService
     - Mock repository dependencies
     - Business logic validation
     - Exception handling scenarios
   - **Integration Testing**
     - @SpringBootTest for full context testing
     - Transaction management testing
     - Service-to-service interaction testing

### Testing Deliverables
- Complete JUnit 5 test suite for entities and repositories
- Mockito-based unit tests for core services
- TestContainers integration tests
- Code coverage report (target: >80%)
- Testing best practices documentation

### Key Files to Implement & Test
- All entity classes (Account, Flight, Ticket, Airport, etc.)
- All repository interfaces
- Core service classes (FlightService, AccountService, TicketService)
- Security configuration and JWT utilities

---

## **Member 2: Trần Xuân Thịnh - API Layer & Integration Testing**

### Primary Responsibilities
1. **Controller Layer Development (Week 1-2)**
   - REST API controller implementation
   - Request/Response DTO mapping
   - API validation and error handling
   - OpenAPI/Swagger documentation

2. **API Testing Suite (Week 3-4)**
   - **Unit Testing Controllers**
     - @WebMvcTest for isolated controller testing
     - MockMvc for HTTP request simulation
     - JSON serialization/deserialization testing
     - Validation testing for request payloads
   - **REST API Integration Testing**
     - @SpringBootTest with TestRestTemplate
     - End-to-end API workflow testing
     - Authentication and authorization testing

3. **Advanced Integration Scenarios (Week 5-6)**
   - **Database Integration Testing**
     - Full stack testing with real database
     - Transaction rollback testing
     - Concurrent request handling
   - **Security Testing**
     - JWT token validation testing
     - Role-based access control testing
     - Authentication failure scenarios

### Testing Deliverables
- Complete @WebMvcTest suite for all controllers
- Integration test suite for API endpoints
- Authentication and authorization test scenarios
- Performance testing for critical endpoints
- API documentation with testing examples

### Key Files to Implement & Test
- All controller classes (FlightController, AuthController, etc.)
- Security filters and JWT authentication
- Exception handlers and error responses
- Integration test configurations

---

## **Member 3: Trần Đức Thịnh - Frontend & E2E Testing**

### Primary Responsibilities
1. **Frontend Application Development (Week 1-2)**
   - React component structure setup
   - TypeScript interfaces and models
   - API service layer with Axios
   - Routing and navigation setup

2. **Frontend Unit & Component Testing (Week 3-4)**
   - **Component Testing with Testing Library**
     - Unit tests for React components
     - User interaction testing (clicks, form submissions)
     - Component state and props testing
     - Mock API calls with MSW
   - **Service Layer Testing**
     - API service function testing
     - Error handling and retry logic testing
     - Authentication service testing

3. **End-to-End & System Testing (Week 5-6)**
   - **Integration Testing**
     - Frontend-Backend integration testing
     - User workflow testing (booking flights, login/logout)
     - Cross-browser compatibility testing
   - **System Testing**
     - Complete user journey testing
     - Performance testing for frontend
     - Accessibility testing

### Testing Deliverables
- Complete Vitest test suite for React components
- API service layer unit tests
- User interaction and workflow tests
- Cross-browser compatibility test results
- Performance and accessibility audit reports

### Key Files to Implement & Test
- All React components (authentication, flight booking, dashboard)
- API service functions and error handling
- Custom hooks and utilities
- Routing and navigation logic

---

## Shared Responsibilities & Collaboration

### Week 1: Project Setup & Planning
**All Members:**
- Project repository setup with proper Git workflow
- CI/CD pipeline configuration (GitHub Actions)
- Code quality tools setup (ESLint, Prettier, SonarQube)
- Testing environment configuration

### Week 6: Integration & Final Testing
**All Members:**
- Cross-team integration testing
- Performance testing and optimization
- Security vulnerability testing
- Documentation and deployment preparation

### Code Review & Quality Assurance
- **Daily:** Peer code reviews for all commits
- **Weekly:** Team sync meetings and progress reviews
- **Milestone Reviews:** Cross-functional testing validation

---

## Testing Metrics & Goals

### Coverage Targets
- **Backend Unit Tests**: >85% line coverage
- **Backend Integration Tests**: >70% branch coverage  
- **Frontend Component Tests**: >80% component coverage
- **E2E Tests**: 100% critical user journeys

### Quality Gates
- All tests must pass before merging to main branch
- No critical security vulnerabilities
- Performance benchmarks must be met
- Code quality score >8.0 (SonarQube)

---

## Timeline Overview

| Week | Member 1 (Phúc Thịnh) | Member 2 (Xuân Thịnh) | Member 3 (Đức Thịnh) |
|------|------------------------|------------------------|----------------------|
| 1-2  | Backend Foundation     | Controller Layer       | Frontend Setup       |
| 3-4  | Entity & Repository Testing | API Testing Suite | Component Testing    |
| 5-6  | Service Layer Testing  | Advanced Integration   | E2E & System Testing |

---

## Development Guidelines

### Testing Best Practices
1. **Follow AAA Pattern**: Arrange, Act, Assert
2. **Test Naming Convention**: `should_ReturnExpectedResult_When_ConditionMet`
3. **One Assertion Per Test**: Keep tests focused and clear
4. **Use Test Data Builders**: Create reusable test data setup
5. **Mock External Dependencies**: Ensure test isolation

### Git Workflow
- **Feature Branches**: `feature/member-name/feature-description`
- **Test Branches**: `test/test-type/component-name`
- **Pull Requests**: Require at least 1 review and passing tests
- **Main Branch**: Protected, deploy-ready code only

### Documentation Requirements
- README with setup instructions
- API documentation with Swagger
- Testing strategy documentation  
- Deployment and maintenance guides

This task division ensures each member has clear ownership while maintaining collaboration opportunities and comprehensive test coverage across the entire application stack.