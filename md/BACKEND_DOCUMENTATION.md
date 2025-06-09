# Flight Management System - Backend Documentation

## Overview
The backend is a comprehensive REST API built with Spring Boot 3.x that manages all aspects of flight operations, bookings, customer management, and administrative functions.

## Technology Stack
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security (currently in demo mode with permitAll)
- **Build Tool**: Maven
- **Additional Libraries**: Lombok, Jackson, MySQL Connector

## Project Structure

```
backend/
├── src/main/java/com/flightmanagement/
│   ├── FlightManagementSystemApplication.java  # Main application class
│   ├── config/                    # Configuration classes
│   │   ├── DataInitializer.java   # Demo data initialization
│   │   ├── DatabaseConfig.java    # JPA configuration
│   │   ├── JacksonConfig.java     # JSON serialization config
│   │   ├── SecurityConfig.java    # Security configuration (demo mode)
│   │   └── WebConfig.java         # CORS and web configuration
│   ├── controller/                # REST API endpoints
│   ├── dto/                       # Data Transfer Objects
│   ├── entity/                    # JPA entities (database models)
│   ├── mapper/                    # Entity-DTO mapping
│   ├── repository/                # Data access layer
│   └── service/                   # Business logic layer
├── src/main/resources/
│   └── application.properties     # Application configuration
└── pom.xml                       # Maven dependencies
```

## Database Architecture

### **IMPORTANT: Account Type Correction**
Based on the actual database schema, the account types are:
- **1**: Customer (not admin as previously documented)
- **2**: Employee (not customer as previously documented)

### Core Entities

#### Parameter
```java
@Entity
@Table(name = "parameter")
public class Parameter {
    @Id @GeneratedValue
    private Integer id;
    private Integer maxMediumAirport;
    private Integer minFlightDuration;      // minutes
    private Integer minLayoverDuration;     // minutes
    private Integer maxLayoverDuration;     // minutes
    private Integer minBookingInAdvanceDuration;
    private Integer maxBookingHoldDuration;
    private LocalDateTime deletedAt;
}
```

#### Account
```java
@Entity
@Table(name = "account")
public class Account {
    @Id @GeneratedValue
    private Integer accountId;
    private String accountName;
    private String password;
    private Integer accountType;    // 1: customer, 2: employee
    private String email;           // Unique
    private String citizenId;       // Unique
    private String phoneNumber;
    private LocalDateTime deletedAt;
}
```

#### Customer
```java
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Integer customerId;     // References Account.accountId
    
    @OneToOne @MapsId
    private Account account;
    
    private Integer score = 0;      // Loyalty points
    private LocalDateTime deletedAt;
}
```

#### Employee
```java
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    private Integer employeeId;     // References Account.accountId
    
    @OneToOne @MapsId
    private Account account;
    
    private Integer employeeType;   // 1-5: Different employee roles
    private LocalDateTime deletedAt;
}
```

#### Airport
```java
@Entity
@Table(name = "airport")
public class Airport {
    @Id @GeneratedValue
    private Integer airportId;
    private String airportName;
    private String cityName;
    private String countryName;
    private LocalDateTime deletedAt;
}
```

#### Plane
```java
@Entity
@Table(name = "plane")
public class Plane {
    @Id @GeneratedValue
    private Integer planeId;
    private String planeCode;       // Unique identifier
    private String planeType;       // Aircraft model
    private Integer seatQuantity;   // Total seats available
    private LocalDateTime deletedAt;
}
```

#### Flight
```java
@Entity
@Table(name = "flight")
public class Flight {
    @Id @GeneratedValue
    private Integer flightId;
    
    @ManyToOne
    private Plane plane;
    
    @ManyToOne
    private Airport departureAirport;
    
    @ManyToOne
    private Airport arrivalAirport;
    
    private String flightCode;      // Flight number
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private LocalDateTime deletedAt;
}
```

#### TicketClass
```java
@Entity
@Table(name = "ticket_class")
public class TicketClass {
    @Id @GeneratedValue
    private Integer ticketClassId;
    private String ticketClassName; // Economy, Business, First Class
    private String color;           // UI display color
    private LocalDateTime deletedAt;
}
```

#### FlightTicketClass (Junction Entity)
```java
@Entity
@Table(name = "flight_ticket_class")
@IdClass(FlightTicketClassId.class)
public class FlightTicketClass {
    @Id
    private Integer flightId;
    
    @Id
    private Integer ticketClassId;
    
    @ManyToOne
    private Flight flight;
    
    @ManyToOne
    private TicketClass ticketClass;
    
    private Integer ticketQuantity;         // Total seats for this class
    private Integer remainingTicketQuantity; // Available seats
    private BigDecimal specifiedFare;       // Price for this flight/class
    private LocalDateTime deletedAt;
}
```

#### Passenger
```java
@Entity
@Table(name = "passenger")
public class Passenger {
    @Id @GeneratedValue
    private Integer passengerId;
    private String passengerName;   // Full name
    private String email;
    private String citizenId;       // Unique
    private String phoneNumber;
    private LocalDateTime deletedAt;
}
```

#### Ticket
```java
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    private Integer ticketId;       // Manual ID assignment required
    
    @ManyToOne
    private Flight flight;
    
    @ManyToOne
    private TicketClass ticketClass;
    
    @ManyToOne
    private Customer bookCustomer;  // Who booked the ticket (nullable)
    
    @ManyToOne
    private Passenger passenger;    // Who will travel
    
    private String seatNumber;      // e.g., "A1", "B15" (max 7 chars)
    private Byte ticketStatus;      // 1: paid, 2: unpaid, 3: canceled
    private LocalDateTime paymentTime; // nullable
    private BigDecimal fare;        // Final price paid
    private LocalDateTime deletedAt;
}
```

#### FlightDetail (Layover Management)
```java
@Entity
@Table(name = "flight_detail")
@IdClass(FlightDetailId.class)
public class FlightDetail {
    @Id
    private Integer flightId;
    
    @Id
    private Integer mediumAirportId; // Layover airport
    
    @ManyToOne
    private Flight flight;
    
    @ManyToOne
    private Airport mediumAirport;
    
    private LocalDateTime arrivalTime;
    private Integer layoverDuration;    // minutes
    private LocalDateTime deletedAt;
}
```

#### Chatbox
```java
@Entity
@Table(name = "chatbox")
public class Chatbox {
    @Id
    private Integer chatboxId;      // Manual ID assignment required
    
    @ManyToOne
    private Customer customer;
    
    @ManyToOne
    private Employee employee;
    
    private LocalDateTime deletedAt;
    
    // Unique constraint on (customer_id, employee_id)
}
```

#### Message
```java
@Entity
@Table(name = "message")
public class Message {
    @Id
    private Integer messageId;      // Manual ID assignment required
    
    @ManyToOne
    private Chatbox chatbox;
    
    private Integer messageType;    // 1: customer to employee, 2: employee to customer
    private String content;         // TEXT field
    private LocalDateTime sendTime;
    private LocalDateTime deletedAt;
}
```

## API Endpoints

### Base URL: `http://localhost:8080/api`

### Authentication & Accounts
```
POST   /accounts/login           # User login
POST   /accounts/register        # User registration
GET    /accounts                 # List all accounts (admin)
GET    /accounts/{id}            # Get account by ID
PUT    /accounts/{id}            # Update account
DELETE /accounts/{id}            # Delete account
GET    /accounts/email/{email}   # Get account by email
```

### Customer Management
```
GET    /customers                # List all customers
GET    /customers/{id}           # Get customer by ID
POST   /customers                # Create customer
PUT    /customers/{id}           # Update customer
DELETE /customers/{id}           # Delete customer
GET    /customers/email/{email}  # Get customer by email
PUT    /customers/{id}/score/{score} # Update loyalty score
```

### Employee Management
```
GET    /employees                # List all employees
GET    /employees/{id}           # Get employee by ID
POST   /employees                # Create employee
PUT    /employees/{id}           # Update employee
DELETE /employees/{id}           # Delete employee
GET    /employees/type/{type}    # Get employees by type
GET    /employees/email/{email}  # Get employee by email
```

### Airport Management
```
GET    /airports                 # List all airports
GET    /airports/{id}            # Get airport by ID
POST   /airports                 # Create airport
PUT    /airports/{id}            # Update airport
DELETE /airports/{id}            # Delete airport
GET    /airports/search/{name}   # Search airports by name
```

### Plane Management
```
GET    /planes                   # List all planes
GET    /planes/{id}              # Get plane by ID
POST   /planes                   # Create plane
PUT    /planes/{id}              # Update plane
DELETE /planes/{id}              # Delete plane
GET    /planes/code/{code}       # Get plane by code
GET    /planes/type/{type}       # Get planes by type
```

### Flight Management
```
GET    /flights                  # List all flights
GET    /flights/{id}             # Get flight by ID
POST   /flights                  # Create flight
PUT    /flights/{id}             # Update flight
DELETE /flights/{id}             # Delete flight
GET    /flights/route?departureAirportId={id}&arrivalAirportId={id}&departureDate={date}
GET    /flights/date-range?startDate={date}&endDate={date}
GET    /flights/search?departureAirportId={id}&arrivalAirportId={id}&departureDate={date}&passengerCount={num}&ticketClassId={id}
```

### Flight Ticket Classes
```
GET    /flight-ticket-classes         # List all flight-ticket-class associations
GET    /flight-ticket-classes/{flightId}/{ticketClassId} # Get specific association
POST   /flight-ticket-classes         # Create association
PUT    /flight-ticket-classes/{flightId}/{ticketClassId} # Update association
DELETE /flight-ticket-classes/{flightId}/{ticketClassId} # Delete association
GET    /flight-ticket-classes/flight/{flightId} # Get classes for flight
GET    /flight-ticket-classes/ticket-class/{ticketClassId} # Get flights for class
GET    /flight-ticket-classes/available # Get available flight-class combinations
PUT    /flight-ticket-classes/{flightId}/{ticketClassId}/update-remaining?quantity={num}
```

### Ticket Classes
```
GET    /ticket-classes           # List all ticket classes
GET    /ticket-classes/{id}      # Get ticket class by ID
POST   /ticket-classes           # Create ticket class
PUT    /ticket-classes/{id}      # Update ticket class
DELETE /ticket-classes/{id}      # Delete ticket class
GET    /ticket-classes/name/{name} # Get ticket class by name
```

### Passenger Management
```
GET    /passengers               # List all passengers
GET    /passengers/{id}          # Get passenger by ID
POST   /passengers               # Create passenger
PUT    /passengers/{id}          # Update passenger
DELETE /passengers/{id}          # Delete passenger
GET    /passengers/citizen-id/{citizenId} # Get passenger by citizen ID
GET    /passengers/email/{email} # Get passengers by email
GET    /passengers/search/{name} # Search passengers by name
```

### Ticket Management & Booking
```
POST   /tickets/book             # Book multiple tickets
GET    /tickets                  # List all tickets
GET    /tickets/{id}             # Get ticket by ID
POST   /tickets                  # Create individual ticket
PUT    /tickets/{id}             # Update ticket
DELETE /tickets/{id}             # Delete ticket
PUT    /tickets/{id}/pay         # Pay for ticket
PUT    /tickets/{id}/cancel      # Cancel ticket
GET    /tickets/flight/{flightId} # Get tickets for flight
GET    /tickets/customer/{customerId} # Get tickets for customer
GET    /tickets/passenger/{passengerId} # Get tickets for passenger
GET    /tickets/status/{status}  # Get tickets by status
GET    /tickets/seat-available?flightId={id}&seatNumber={seat} # Check seat availability
```

### Chat System
```
GET    /chatboxes                # List all chatboxes
GET    /chatboxes/sorted-by-customer-time # List chatboxes sorted by latest customer message
GET    /chatboxes/{id}           # Get chatbox by ID
POST   /chatboxes                # Create chatbox
DELETE /chatboxes/{id}           # Delete chatbox
GET    /chatboxes/customer/{customerId} # Get chatboxes for customer
GET    /chatboxes/employee/{employeeId} # Get chatboxes for employee
GET    /chatboxes/customer/{customerId}/employee/{employeeId} # Get specific chatbox
POST   /chatboxes/get-or-create?customerId={id}&employeeId={id} # Get or create chatbox

GET    /messages                 # List all messages
GET    /messages/{id}            # Get message by ID
POST   /messages                 # Send message
DELETE /messages/{id}            # Delete message
GET    /messages/chatbox/{chatboxId} # Get messages for chatbox
```

### System Parameters
```
GET    /parameters               # Get current parameters
PUT    /parameters/{id}          # Update parameters
GET    /parameters/max-medium-airports # Get max medium airports
PUT    /parameters/max-medium-airports/{value} # Update max medium airports
GET    /parameters/min-flight-duration # Get min flight duration
PUT    /parameters/min-flight-duration/{value} # Update min flight duration
PUT    /parameters/min-layover/{value} # Update min layover duration
PUT    /parameters/max-layover/{value} # Update max layover duration
PUT    /parameters/min-booking-advance/{value} # Update min booking advance
PUT    /parameters/max-booking-hold/{value} # Update max booking hold
POST   /parameters/initialize    # Initialize default parameters
```

### Flight Details (Layover Management)
```
GET    /flight-details           # List all flight details
POST   /flight-details           # Create flight detail
PUT    /flight-details/{flightId}/{mediumAirportId} # Update flight detail
DELETE /flight-details/{flightId}/{mediumAirportId} # Delete flight detail
GET    /flight-details/flight/{flightId} # Get details for flight
```

### Demo & Health Endpoints
```
GET    /demo/health              # System health check
GET    /demo/info                # System information
```

## Business Logic

### Account Types (CORRECTED)
- **1**: Customer - Access to booking and personal ticket management
- **2**: Employee - Access to admin panels and management functions

### Employee Types (for accountType = 2)
1. **Flight Schedule Reception** (tiep nhan lich bay) - Manage flight schedules
2. **Ticket Sales/Booking** (ban/dat ve) - Handle reservations and sales  
3. **Customer Service** (cskh) - Support and chat
4. **Accounting** (ke toan) - Financial operations
5. **System Administrator** (sa) - Full system access

### Ticket Status
- **1**: Paid - Payment completed
- **2**: Unpaid - Booking confirmed, payment pending (default)
- **3**: Canceled - Ticket canceled, refund processed

### Message Types
- **1**: Customer sends to employee
- **2**: Employee sends to customer

## Database Constraints & Considerations

### Manual ID Assignment Required
These entities require manual ID assignment (not auto-generated):
- **Ticket**: `ticket_id` must be manually assigned
- **Chatbox**: `chatbox_id` must be manually assigned  
- **Message**: `message_id` must be manually assigned

### Unique Constraints
- **Account**: `email` and `citizen_id` must be unique
- **Passenger**: `citizen_id` must be unique
- **Chatbox**: Unique constraint on `(customer_id, employee_id)` combination

### Nullable Fields
- **Ticket**: `book_customer_id` and `payment_time` can be null
- All entities have `deleted_at` for soft deletes

### Field Size Limits
- **Seat Number**: Maximum 7 characters (e.g., "A1", "B15")
- Various string fields limited to 200 characters
- **Message Content**: TEXT field for longer messages

## Demo Accounts (Updated for Correct Account Types)

#### Customer Accounts (accountType = 1)
```
Email: customer@flightms.com
Password: customer123
Account Type: 1 (Customer)
Access: Flight booking, ticket management

Email: john.doe@email.com
Password: password123
Account Type: 1 (Customer)
Access: Flight booking, ticket management
```

#### Employee/Admin Accounts (accountType = 2)
```
Email: admin@flightms.com
Password: admin123
Account Type: 2 (Employee/Admin)
Employee Type: 5 (System Administrator)
Access: Full system administration

Email: employee@flightms.com
Password: employee123
Account Type: 2 (Employee)
Employee Type: 2 (Ticket Sales/Booking)
Access: Flight operations, customer service
```

## Critical Implementation Notes

### Frontend-Backend Account Type Mapping
**IMPORTANT**: The frontend code currently assumes the opposite mapping:
- Frontend expects `accountType: 1` for admin/employee
- Frontend expects `accountType: 2` for customer

**This requires either:**
1. **Database update** to match frontend expectations, OR
2. **Frontend update** to match database schema

### ID Generation Strategy
Manual ID assignment entities need proper ID generation logic:
```java
// Example for Ticket ID generation
@Service
public class TicketService {
    public Integer generateTicketId() {
        // Implementation needed for unique ID generation
        return ticketRepository.findMaxTicketId() + 1;
    }
}
```

### Booking Process Considerations
1. **Seat Assignment**: Must respect 7-character seat number limit
2. **Customer Booking**: `book_customer_id` can be null for walk-in bookings
3. **Payment Flow**: Tickets default to unpaid status (2), require payment processing
4. **Passenger Creation**: Handle duplicate `citizen_id` constraints

This documentation now accurately reflects the actual database schema and highlights critical discrepancies that need to be addressed for proper system integration.
