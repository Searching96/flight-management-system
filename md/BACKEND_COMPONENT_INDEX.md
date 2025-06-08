# Backend Component Index

## Overview
This document provides a complete index of backend components based on the `fms_db.sql` database schema. The backend is fully implemented for all 14 database tables.

## Database Tables (14 Total)
Based on `fms_db.sql` analysis:

1. **parameter** - System configuration parameters
2. **account** - User accounts (customers and employees)
3. **employee** - Employee-specific data extending account
4. **customer** - Customer-specific data extending account
5. **passenger** - Flight passenger information
6. **plane** - Aircraft information
7. **airport** - Airport master data
8. **ticket_class** - Flight class definitions (Economy, Business, etc.)
9. **flight** - Flight schedule information
10. **flight_detail** - Flight leg details for multi-leg flights
11. **flight_ticket_class** - Available classes per flight with pricing
12. **ticket** - Booking and ticket information (includes payment data)
13. **chatbox** - Customer-employee chat sessions
14. **message** - Chat messages

## Backend Component Status

### ✅ FULLY IMPLEMENTED COMPONENTS

#### Entities (14/14)
- [x] **Parameter.java** - System parameters entity
- [x] **Account.java** - Base account entity
- [x] **Employee.java** - Employee entity (extends account via foreign key)
- [x] **Customer.java** - Customer entity (extends account via foreign key)
- [x] **Passenger.java** - Passenger information entity
- [x] **Plane.java** - Aircraft entity
- [x] **Airport.java** - Airport entity
- [x] **TicketClass.java** - Flight class entity
- [x] **Flight.java** - Flight entity
- [x] **FlightDetail.java** - Flight detail entity with composite key
- [x] **FlightTicketClass.java** - Flight-class pricing entity
- [x] **Ticket.java** - Ticket entity (includes payment_time and ticket_status)
- [x] **Chatbox.java** - Chat session entity
- [x] **Message.java** - Chat message entity

#### DTOs (14/14)
- [x] **ParameterDto.java**
- [x] **AccountDto.java**
- [x] **EmployeeDto.java**
- [x] **CustomerDto.java**
- [x] **PassengerDto.java**
- [x] **PlaneDto.java**
- [x] **AirportDto.java**
- [x] **TicketClassDto.java**
- [x] **FlightDto.java**
- [x] **FlightDetailDto.java**
- [x] **FlightTicketClassDto.java**
- [x] **TicketDto.java**
- [x] **ChatboxDto.java**
- [x] **MessageDto.java**

#### Repositories (14/14)
- [x] **ParameterRepository.java** - System parameter data access
- [x] **AccountRepository.java** - Account data access with authentication support
- [x] **EmployeeRepository.java** - Employee data access with type filtering
- [x] **CustomerRepository.java** - Customer data access with scoring
- [x] **PassengerRepository.java** - Passenger data access
- [x] **PlaneRepository.java** - Aircraft data access
- [x] **AirportRepository.java** - Airport data access with search capabilities
- [x] **TicketClassRepository.java** - Flight class data access
- [x] **FlightRepository.java** - Flight data access with complex search
- [x] **FlightDetailRepository.java** - Flight detail data access
- [x] **FlightTicketClassRepository.java** - Flight-class pricing data access
- [x] **TicketRepository.java** - Ticket data access with payment status tracking
- [x] **ChatboxRepository.java** - Chat session data access
- [x] **MessageRepository.java** - Chat message data access

#### Services (14/14)
- [x] **ParameterService.java** + **ParameterServiceImpl.java**
- [x] **AccountService.java** + **AccountServiceImpl.java** (includes authentication)
- [x] **EmployeeService.java** + **EmployeeServiceImpl.java**
- [x] **CustomerService.java** + **CustomerServiceImpl.java**
- [x] **PassengerService.java** + **PassengerServiceImpl.java**
- [x] **PlaneService.java** + **PlaneServiceImpl.java**
- [x] **AirportService.java** + **AirportServiceImpl.java**
- [x] **TicketClassService.java** + **TicketClassServiceImpl.java**
- [x] **FlightService.java** + **FlightServiceImpl.java**
- [x] **FlightDetailService.java** + **FlightDetailServiceImpl.java**
- [x] **FlightTicketClassService.java** + **FlightTicketClassServiceImpl.java**
- [x] **TicketService.java** + **TicketServiceImpl.java** (includes payment processing)
- [x] **ChatboxService.java** + **ChatboxServiceImpl.java**
- [x] **MessageService.java** + **MessageServiceImpl.java**

#### Controllers (14/14)
- [x] **ParameterController.java** - System parameter REST endpoints
- [x] **AccountController.java** - Account management and authentication REST endpoints
- [x] **EmployeeController.java** - Employee management REST endpoints
- [x] **CustomerController.java** - Customer management REST endpoints
- [x] **PassengerController.java** - Passenger management REST endpoints
- [x] **PlaneController.java** - Aircraft management REST endpoints
- [x] **AirportController.java** - Airport management REST endpoints
- [x] **TicketClassController.java** - Flight class management REST endpoints
- [x] **FlightController.java** - Flight management and search REST endpoints
- [x] **FlightDetailController.java** - Flight detail management REST endpoints
- [x] **FlightTicketClassController.java** - Flight-class pricing REST endpoints
- [x] **TicketController.java** - Ticket booking and payment REST endpoints
- [x] **ChatboxController.java** - Chat session REST endpoints
- [x] **MessageController.java** - Chat message REST endpoints

#### Mappers (14/14)
- [x] **ParameterMapper.java**
- [x] **AccountMapper.java**
- [x] **EmployeeMapper.java**
- [x] **CustomerMapper.java**
- [x] **PassengerMapper.java**
- [x] **PlaneMapper.java**
- [x] **AirportMapper.java**
- [x] **TicketClassMapper.java**
- [x] **FlightMapper.java**
- [x] **FlightDetailMapper.java**
- [x] **FlightTicketClassMapper.java**
- [x] **TicketMapper.java**
- [x] **ChatboxMapper.java**
- [x] **MessageMapper.java**

## Key Integration Points

### Payment Processing
- **Implementation**: Handled directly in `TicketService` and `Ticket` entity
- **Database Fields**: `ticket.payment_time` (nullable), `ticket.ticket_status` (1=paid, 2=unpaid, 3=canceled)
- **Business Logic**: Payment status tracking without separate payment entity

### Authentication & Authorization
- **Implementation**: `AccountService` with role-based access
- **Account Types**: 1=customer, 2=employee
- **Employee Types**: 1=flight scheduling, 2=booking/ticketing, 3=customer service, 4=accounting, 5=system admin

### Communication System
- **Implementation**: Full chat system with `ChatboxService` and `MessageService`
- **Message Types**: 1=customer to employee, 2=employee to customer
- **Unique Constraints**: One chatbox per customer-employee pair

### System Configuration
- **Implementation**: `ParameterService` for system-wide settings
- **Configurable**: Flight duration limits, booking advance requirements, layover constraints

## Backend Completeness Assessment

### ✅ COMPLETE FEATURES
1. **User Management**: Full account, employee, customer management
2. **Flight Management**: Complete flight scheduling, airport, aircraft management
3. **Booking System**: Full ticket booking with class selection and pricing
4. **Payment Processing**: Integrated payment status tracking in tickets
5. **Communication**: Complete customer service chat system
6. **System Configuration**: Configurable business rules and parameters
7. **Data Integrity**: Proper foreign key relationships and soft deletes

### 🎯 BUSINESS LOGIC COVERAGE
- **Flight Search**: Complex multi-criteria search with layovers
- **Booking Flow**: Complete booking process from search to payment
- **Role-Based Access**: Different employee types with specific permissions
- **Customer Scoring**: Customer loyalty point system
- **Audit Trail**: Soft delete pattern with `deleted_at` timestamps
- **Business Rules**: Configurable constraints via parameter system

## Architecture Patterns Used

### 1. **Layered Architecture**
```
Controller Layer → Service Layer → Repository Layer → Entity Layer
```

### 2. **Data Transfer Objects (DTOs)**
- Clean separation between internal entities and API contracts
- Consistent mapping via dedicated mapper classes

### 3. **Repository Pattern**
- Spring Data JPA repositories with custom query methods
- Consistent active record filtering (non-deleted records)

### 4. **Service Layer Pattern**
- Business logic encapsulation
- Transaction management
- Cross-cutting concern handling

### 5. **Soft Delete Pattern**
- All entities support soft deletion via `deleted_at` field
- Repository methods filter out deleted records by default

## Testing Coverage
- **Unit Tests**: Service layer implementations tested
- **Integration Tests**: Full booking flow tested
- **Controller Tests**: REST endpoint testing
- **Repository Tests**: Data access layer testing

## Conclusion
The backend is **FULLY IMPLEMENTED** and production-ready for all 14 database tables. No additional components are needed to make the backend independent. The system supports:

- Complete flight management operations
- Full booking and payment processing
- Customer service chat system
- User management with role-based access
- System configuration management
- Comprehensive business rule enforcement

The backend can operate independently and provides a complete REST API for the frontend application.
