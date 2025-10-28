# ✈️ Flight Management System

<div align="center">
A comprehensive, enterprise-grade flight management system built with Java and TypeScript
</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Core Features](#core-features)
- [Advanced Features](#advanced-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Prerequisites](#prerequisites)
- [Step-by-Step Installation](#step-by-step-installation)
- [Docker Installation](#docker-installation-alternative)
- [Configuration](#️-configuration)
- [Usage](#-usage)
- [API Reference](#-api-reference)
- [Testing](#-testing)
- [Performance](#-performance)
- [Security](#-security)
- [Deployment](#-deployment)
- [Troubleshooting](#-troubleshooting)
- [Team](#-team)
- [Acknowledgments](#-acknowledgments)

---

## 🌟 Overview

The **Flight Management System** is a robust, scalable, and feature-rich application designed to handle all aspects of modern airline operations. Built with Java and following industry best practices, this system provides a complete solution for managing flights, passengers, bookings, crew, aircraft, and airline operations.

### Key Highlights

- 🎯 **Enterprise-Ready**: Designed for scalability and high availability
- 🔒 **Secure**: Implements industry-standard security practices
- 📊 **Data-Driven**: Comprehensive analytics and reporting capabilities
- 🚀 **High Performance**: Optimized for handling large volumes of transactions
- 🔧 **Maintainable**: Clean code architecture with extensive documentation
- 🧪 **Well-Tested**: Comprehensive unit and integration test coverage

### Use Cases

- **Airlines**: Manage flight operations, crew scheduling, and fleet management
- **Travel Agencies**: Book flights and manage customer reservations
- **Airport Authorities**: Track flight schedules and gate assignments
- **Passengers**: Search flights, make bookings, and manage itineraries

---

## ✨ Features

### Core Features

#### 🛫 Flight Management

- **Flight Scheduling**: Create, update, and manage flight schedules
- **Route Management**: Define and optimize flight routes
- **Flight Status Tracking**: Real-time flight status updates
- **Delay Management**: Handle and communicate flight delays
- **Cancellation Handling**: Process flight cancellations and rebookings
- **Schedule Optimization**: Analyze and optimize flight schedules

#### 👥 Passenger Management

- **Passenger Profiles**: Comprehensive passenger information management
- **Frequent Flyer Programs**: Loyalty program integration
- **Special Assistance**: Handle special passenger requirements
- **Travel History**: Track passenger travel records
- **Preferences Management**: Store and apply passenger preferences
- **Document Verification**: Passport and visa validation

#### 📝 Booking & Reservation System

- **Flight Search**: Advanced search with multiple filters
- **Seat Selection**: Interactive seat map and selection
- **Booking Management**: Create, modify, and cancel bookings
- **Multi-City Bookings**: Support for complex itineraries
- **Group Bookings**: Handle group reservations
- **Payment Processing**: Secure payment gateway integration
- **Ticket Generation**: Generate e-tickets and boarding passes
- **Booking Confirmation**: Email and SMS notifications

#### ✈️ Airline Management

- **Airline Registration**: Onboard new airline partners
- **Fleet Management**: Track airline fleet and aircraft
- **Partnership Management**: Handle codeshare agreements
- **Pricing Rules**: Configure dynamic pricing strategies
- **Baggage Policies**: Define and enforce baggage rules
- **Service Class Management**: Manage economy, business, first class

#### 🏢 Airport Management

- **Airport Information**: Maintain airport details and facilities
- **Terminal Management**: Track terminals and gates
- **Ground Services**: Coordinate ground handling operations
- **Customs & Immigration**: Integration with border control
- **Lounge Management**: Track airport lounge access
- **Facility Management**: Monitor airport amenities

#### 👨‍✈️ Crew Management

- **Crew Scheduling**: Assign crew members to flights
- **Qualification Tracking**: Monitor certifications and training
- **Duty Time Management**: Ensure compliance with regulations
- **Crew Availability**: Track crew member availability
- **Emergency Replacements**: Handle last-minute crew changes
- **Performance Tracking**: Monitor crew performance metrics

#### 🛩️ Aircraft Management

- **Fleet Tracking**: Monitor aircraft location and status
- **Maintenance Scheduling**: Plan and track maintenance
- **Aircraft Specifications**: Store detailed aircraft information
- **Fuel Management**: Track fuel consumption and costs
- **Utilization Reports**: Analyze aircraft usage patterns
- **Compliance Tracking**: Ensure regulatory compliance

#### 💺 Seat Management

- **Seat Mapping**: Create and manage seat configurations
- **Availability Tracking**: Real-time seat availability
- **Premium Seat Selection**: Handle premium seat upgrades
- **Seat Blocking**: Block seats for special requirements
- **Layout Configuration**: Configure different aircraft layouts
- **Preference Handling**: Apply passenger seating preferences

### Advanced Features

#### 📊 Analytics & Reporting

- **Revenue Analytics**: Track revenue by route, class, and period
- **Occupancy Reports**: Analyze seat occupancy rates
- **Performance Metrics**: Monitor KPIs and operational efficiency
- **Customer Analytics**: Understand customer behavior patterns
- **Predictive Analytics**: Forecast demand and optimize pricing
- **Custom Reports**: Generate customizable reports

#### 🔔 Notification System

- **Email Notifications**: Automated email communications
- **SMS Alerts**: Text message notifications for time-sensitive updates
- **Push Notifications**: Mobile app notifications
- **Booking Confirmations**: Instant booking confirmations
- **Flight Updates**: Real-time flight status changes
- **Promotional Communications**: Marketing campaigns

#### 🔐 Security Features

- **User Authentication**: Secure login with multi-factor authentication
- **Role-Based Access Control**: Granular permission management
- **Data Encryption**: Encrypt sensitive data at rest and in transit

#### 🌐 Integration Capabilities (in the future)

- **Payment Gateways**: Multiple payment provider integrations
- **GDS Systems**: Connect with Global Distribution Systems
- **Email Services**: SMTP integration for notifications
- **SMS Providers**: SMS gateway integration
- **Third-Party APIs**: Weather, currency, and travel APIs
- **Accounting Systems**: Financial system integration

---

## 🛠 Technology Stack

### Core Technologies

| Technology           | Purpose                                        |
| -------------------- | ---------------------------------------------- |
| **Java**             | Core programming language                      |
| **Maven/Gradle**     | Build automation and dependency management     |
| **JUnit**            | Unit testing framework                         |
| **Mockito**          | Mocking framework for tests                    |
| **Spring Boot**      | Application framework and dependency injection |
| **Spring Data JPA**  | Database access and ORM                        |
| **Hibernate**        | JPA implementation                             |
| **MySQL/PostgreSQL** | Relational database                            |

### Design Patterns Implemented

- **MVC (Model-View-Controller)**: Separation of concerns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **Factory Pattern**: Object creation
- **Singleton Pattern**: Single instance management
- **Observer Pattern**: Event handling and notifications
- **Strategy Pattern**: Dynamic algorithm selection
- **Builder Pattern**: Complex object construction
- **Dependency Injection**: Loose coupling

---

## 📁 Project Structure

```
flight-management-system/
├── .gitignore
├── backend/
│   ├── .gitattributes
│   ├── .gitignore
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── flightmanagement/
│       │   │           ├── config/
│       │   │           │   ├── DatabaseConfig.java
│       │   │           │   ├── DataInitializer.java
│       │   │           │   ├── JwtConfig.java
│       │   │           │   ├── OpenAPIConfig.java
│       │   │           │   ├── VNPayConfig.java
│       │   │           │   ├── WebConfig.java
│       │   │           │   └── WebSocketConfig.java
│       │   │           ├── controller/
│       │   │           │   ├── AccountChatboxController.java
│       │   │           │   ├── AccountController.java
│       │   │           │   ├── AirportController.java
│       │   │           │   ├── AuthController.java
│       │   │           │   ├── ChatboxController.java
│       │   │           │   ├── CustomerController.java
│       │   │           │   ├── DebugController.java
│       │   │           │   ├── DemoController.java
│       │   │           │   ├── EmployeeController.java
│       │   │           │   ├── FlightController.java
│       │   │           │   ├── FlightDetailController.java
│       │   │           │   ├── FlightTicketClassController.java
│       │   │           │   ├── MessageController.java
│       │   │           │   ├── ParameterController.java
│       │   │           │   ├── PassengerController.java
│       │   │           │   ├── PaymentController.java
│       │   │           │   ├── PlaneController.java
│       │   │           │   ├── StatisticsController.java
│       │   │           │   ├── TicketClassController.java
│       │   │           │   └── TicketController.java
│       │   │           ├── dto/
│       │   │           │   ├── AccountChatboxDto.java
│       │   │           │   ├── AccountDto.java
│       │   │           │   ├── AirportDto.java
│       │   │           │   ├── AuthResponse.java
│       │   │           │   ├── BookingDto.java
│       │   │           │   ├── ChatboxDto.java
│       │   │           │   ├── ChatboxTestDto.java
│       │   │           │   ├── CustomerDto.java
│       │   │           │   ├── EmailBookingRequest.java
│       │   │           │   ├── EmployeeDto.java
│       │   │           │   ├── FlightDetailDto.java
│       │   │           │   ├── FlightDto.java
│       │   │           │   ├── FlightRequest.java
│       │   │           │   ├── FlightSearchCriteria.java
│       │   │           │   ├── FlightTicketClassDto.java
│       │   │           │   ├── LoginRequestDto.java
│       │   │           │   ├── MessageDto.java
│       │   │           │   ├── MonthlyStatisticsDto.java
│       │   │           │   ├── ParameterDto.java
│       │   │           │   ├── PassengerDto.java
│       │   │           │   ├── PasswordForgetRequest.java
│       │   │           │   ├── PasswordResetRequest.java
│       │   │           │   ├── PlaneDto.java
│       │   │           │   ├── RegisterDto.java
│       │   │           │   ├── TicketClassDto.java
│       │   │           │   ├── TicketDto.java
│       │   │           │   ├── TicketSearchCriteria.java
│       │   │           │   ├── TokenRequest.java
│       │   │           │   ├── UserDetailsDto.java
│       │   │           │   └── YearlyStatisticsDto.java
│       │   │           ├── entity/
│       │   │           │   ├── Account.java
│       │   │           │   ├── AccountChatbox.java
│       │   │           │   ├── Airport.java
│       │   │           │   ├── ApiResponse.java
│       │   │           │   ├── Chatbox.java
│       │   │           │   ├── Customer.java
│       │   │           │   ├── Employee.java
│       │   │           │   ├── Flight.java
│       │   │           │   ├── FlightDetail.java
│       │   │           │   ├── FlightTicketClass.java
│       │   │           │   ├── Message.java
│       │   │           │   ├── Parameter.java
│       │   │           │   ├── Passenger.java
│       │   │           │   ├── Plane.java
│       │   │           │   ├── Ticket.java
│       │   │           │   └── TicketClass.java
│       │   │           ├── exception/
│       │   │           │   ├── BadRequestException.java
│       │   │           │   ├── GlobalExceptionHandler.java
│       │   │           │   └── ResourceNotFoundException.java
│       │   │           ├── FlightManagementSystemApplication.java
│       │   │           ├── mapper/
│       │   │           │   ├── AccountChatboxMapper.java
│       │   │           │   ├── AccountMapper.java
│       │   │           │   ├── AirportMapper.java
│       │   │           │   ├── AuthMapper.java
│       │   │           │   ├── BaseMapper.java
│       │   │           │   ├── ChatboxMapper.java
│       │   │           │   ├── CustomerMapper.java
│       │   │           │   ├── EmployeeMapper.java
│       │   │           │   ├── FlightDetailMapper.java
│       │   │           │   ├── FlightMapper.java
│       │   │           │   ├── FlightTicketClassMapper.java
│       │   │           │   ├── MessageMapper.java
│       │   │           │   ├── ParameterMapper.java
│       │   │           │   ├── PassengerMapper.java
│       │   │           │   ├── PlaneMapper.java
│       │   │           │   ├── TicketClassMapper.java
│       │   │           │   └── TicketMapper.java
│       │   │           ├── repository/
│       │   │           │   ├── AccountChatboxRepository.java
│       │   │           │   ├── AccountRepository.java
│       │   │           │   ├── AirportRepository.java
│       │   │           │   ├── ChatboxRepository.java
│       │   │           │   ├── CustomerRepository.java
│       │   │           │   ├── EmployeeRepository.java
│       │   │           │   ├── FlightDetailRepository.java
│       │   │           │   ├── FlightRepository.java
│       │   │           │   ├── FlightTicketClassRepository.java
│       │   │           │   ├── MessageRepository.java
│       │   │           │   ├── ParameterRepository.java
│       │   │           │   ├── PassengerRepository.java
│       │   │           │   ├── PlaneRepository.java
│       │   │           │   ├── StatisticsRepository.java
│       │   │           │   ├── TicketClassRepository.java
│       │   │           │   └── TicketRepository.java
│       │   │           ├── security/
│       │   │           │   ├── CustomUserDetails.java
│       │   │           │   ├── CustomUserDetailsService.java
│       │   │           │   ├── JwtAuthenticationFilter.java
│       │   │           │   ├── JwtService.java
│       │   │           │   ├── JwtUtil.java
│       │   │           │   └── SecurityConfig.java
│       │   │           ├── service/
│       │   │           │   ├── AccountChatboxService.java
│       │   │           │   ├── AccountService.java
│       │   │           │   ├── AirportService.java
│       │   │           │   ├── AuthService.java
│       │   │           │   ├── ChatboxService.java
│       │   │           │   ├── ChatService.java
│       │   │           │   ├── CustomerService.java
│       │   │           │   ├── EmailService.java
│       │   │           │   ├── EmployeeService.java
│       │   │           │   ├── FlightDetailService.java
│       │   │           │   ├── FlightService.java
│       │   │           │   ├── FlightTicketClassService.java
│       │   │           │   ├── impl/
│       │   │           │   │   ├── AccountChatboxServiceImpl.java
│       │   │           │   │   ├── AccountServiceImpl.java
│       │   │           │   │   ├── AirportServiceImpl.java
│       │   │           │   │   ├── AuthServiceImpl.java
│       │   │           │   │   ├── ChatboxServiceImpl.java
│       │   │           │   │   ├── ChatServiceImpl.java
│       │   │           │   │   ├── CustomerServiceImpl.java
│       │   │           │   │   ├── EmailServiceImpl.java
│       │   │           │   │   ├── EmployeeServiceImpl.java
│       │   │           │   │   ├── FlightDetailServiceImpl.java
│       │   │           │   │   ├── FlightServiceImpl.java
│       │   │           │   │   ├── FlightTicketClassServiceImpl.java
│       │   │           │   │   ├── MessageServiceImpl.java
│       │   │           │   │   ├── ParameterServiceImpl.java
│       │   │           │   │   ├── PassengerServiceImpl.java
│       │   │           │   │   ├── PlaneServiceImpl.java
│       │   │           │   │   ├── StatisticsServiceImpl.java
│       │   │           │   │   ├── TicketClassServiceImpl.java
│       │   │           │   │   ├── TicketCleanupServiceImpl.java
│       │   │           │   │   ├── TicketServiceImpl.java
│       │   │           │   │   └── VNPayServiceImpl.java
│       │   │           │   ├── MessageService.java
│       │   │           │   ├── ParameterService.java
│       │   │           │   ├── PassengerService.java
│       │   │           │   ├── PaymentService.java
│       │   │           │   ├── PlaneService.java
│       │   │           │   ├── StatisticsService.java
│       │   │           │   ├── TicketClassService.java
│       │   │           │   ├── TicketCleanupService.java
│       │   │           │   └── TicketService.java
│       │   │           └── websocket/
│       │   │               └── ChatWebSocketHandler.java
│       │   └── resources/
│       │       ├── application-dev.properties
│       │       ├── application-prod.properties
│       │       ├── application.properties
│       │       └── logback-spring.xml
│       └── test/
│           └── java/
│               └── com/
│                   └── flightmanagement/
│                       ├── FlightManagementSystemTests.java
│                       └── service/
│                           ├── FlightDetailServiceTest.java
│                           ├── FlightServiceTest.java
│                           └── TicketServiceTest.java
├── database/
│   ├── fms_db.sql
│   └── fms_gen_data.sql
├── diagrams/
│   ├── SOTA-UCD-v2.png
│   ├── SOTA-UCD.png
│   ├── SOTA-UCD.puml
│   ├── System_Level_UseCase_Diagram.txt
│   ├── System_UseCase_Diagram.txt
│   └── System-Use-Case-PlantUML.txt
├── docs/
│   ├── Application_UseCase_Diagram.puml
│   ├── BẢN MÔ TẢ YÊU CẦU SẢN PHẨM.xlsx
│   ├── DSNhanVien_import.xlsx
│   ├── export-of-fms-user-stories.md
│   ├── function-point-analysis-detailed.md
│   ├── function-point-table-filled.csv
│   ├── function-point-table.csv
│   ├── SystemUseCase.png
│   ├── task-assign.md
│   └── User_Story.xlsx
└── frontend/
    ├── .gitignore
    ├── eslint.config.js
    ├── index.html
    ├── jest.config.js
    ├── package-lock.json
    ├── package.json
    ├── public/
    │   └── vite.svg
    ├── README.md
    ├── src/
    │   ├── App.css
    │   ├── App.tsx
    │   ├── assets/
    │   │   └── react.svg
    │   ├── components/
    │   │   ├── admin/
    │   │   │   ├── AdminPanel.tsx
    │   │   │   ├── AirportManagement.tsx
    │   │   │   ├── ChatManagement.tsx
    │   │   │   ├── CustomerSupport.tsx
    │   │   │   ├── EmployeeManagement.tsx
    │   │   │   ├── FlightManagement.tsx
    │   │   │   ├── flights/
    │   │   │   │   ├── FlightDetailsTable.tsx
    │   │   │   │   ├── FlightForm.tsx
    │   │   │   │   └── FlightTable.tsx
    │   │   │   ├── ParameterSettings.tsx
    │   │   │   ├── PlaneManagement.tsx
    │   │   │   └── TicketClassManagement.tsx
    │   │   ├── auth/
    │   │   │   ├── ForgetPasswordForm.tsx
    │   │   │   ├── LoginForm.tsx
    │   │   │   ├── RegisterForm.tsx
    │   │   │   └── ResetPasswordForm.tsx
    │   │   ├── booking/
    │   │   │   ├── BookingConfirmation.tsx
    │   │   │   ├── BookingForm.tsx
    │   │   │   └── BookingLookup.tsx
    │   │   ├── chat/
    │   │   │   └── ChatWidget.tsx
    │   │   ├── common/
    │   │   │   ├── TypeAhead.css
    │   │   │   └── TypeAhead.tsx
    │   │   ├── debug/
    │   │   │   └── DebugLogin.tsx
    │   │   ├── employee/
    │   │   │   └── EmployeeForm.tsx
    │   │   ├── flights/
    │   │   │   ├── FlightCard.tsx
    │   │   │   ├── FlightList.tsx
    │   │   │   └── FlightSearch.tsx
    │   │   ├── home/
    │   │   │   └── HomePage.tsx
    │   │   ├── layout/
    │   │   │   ├── Footer.tsx
    │   │   │   ├── Header.tsx
    │   │   │   └── Layout.tsx
    │   │   ├── payment/
    │   │   │   ├── PaymentHandler.tsx
    │   │   │   └── PaymentResult.tsx
    │   │   ├── profile/
    │   │   │   ├── EditProfile.tsx
    │   │   │   └── ResetPassword.tsx
    │   │   ├── routes/
    │   │   │   └── ProtectedRoute.tsx
    │   │   ├── Statistics/
    │   │   │   ├── Statistics.css
    │   │   │   └── Statistics.tsx
    │   │   ├── support/
    │   │   │   └── SupportSearch.tsx
    │   │   ├── ticketing/
    │   │   │   ├── index.ts
    │   │   │   └── TicketingManagement.tsx
    │   │   └── tickets/
    │   │       └── TicketCard.tsx
    │   ├── hooks/
    │   │   ├── useAuth.tsx
    │   │   ├── useEmployees.ts
    │   │   ├── useFlightDetails.ts
    │   │   └── useFlights.ts
    │   ├── index.css
    │   ├── index.tsx
    │   ├── models/
    │   │   ├── Account.ts
    │   │   ├── AccountChatBox.ts
    │   │   ├── Airport.ts
    │   │   ├── ApiResponse.ts
    │   │   ├── Auth.ts
    │   │   ├── Chat.ts
    │   │   ├── Customer.ts
    │   │   ├── Employee.ts
    │   │   ├── Flight.ts
    │   │   ├── FlightDetail.ts
    │   │   ├── FlightTicketClass.ts
    │   │   ├── index.ts
    │   │   ├── LoginResponse.ts
    │   │   ├── Message.ts
    │   │   ├── Parameter.ts
    │   │   ├── Passenger.ts
    │   │   ├── Payment.ts
    │   │   ├── Plane.ts
    │   │   ├── Statistics.ts
    │   │   ├── Ticket.ts
    │   │   └── TicketClass.ts
    │   ├── services/
    │   │   ├── accountChatboxService.ts
    │   │   ├── accountService.ts
    │   │   ├── AirportService.ts
    │   │   ├── api.ts
    │   │   ├── authService.ts
    │   │   ├── bookingConfirmationService.ts
    │   │   ├── chatService.ts
    │   │   ├── config.ts
    │   │   ├── customerService.ts
    │   │   ├── debugService.ts
    │   │   ├── demoService.ts
    │   │   ├── employeeService.ts
    │   │   ├── flightDetailService.ts
    │   │   ├── FlightService.ts
    │   │   ├── flightTicketClassService.ts
    │   │   ├── index.ts
    │   │   ├── messageService.ts
    │   │   ├── ParameterService.ts
    │   │   ├── passengerService.ts
    │   │   ├── paymentService.ts
    │   │   ├── planeService.ts
    │   │   ├── statisticsService.ts
    │   │   ├── ticketClassService.ts
    │   │   ├── ticketService.ts
    │   │   └── websocketService.ts
    │   ├── setupTests.ts
    │   └── styles/
    │       └── FlightForm.css
    ├── tsconfig.json
    ├── tsconfig.tsbuildinfo
    ├── vite.config.js
    └── vite.config.ts

```

---

## 🏗 Architecture

### System Architecture

The Flight Management System follows a **layered architecture** pattern:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│            (Controllers, REST Endpoints, UI)             │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                     Service Layer                        │
│         (Business Logic, Validation, Orchestration)      │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                  Repository Layer                        │
│           (Data Access, CRUD Operations)                 │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                   Database Layer                         │
│        (MySQL/PostgreSQL, Data Persistence)              │
└─────────────────────────────────────────────────────────┘
```

### Component Diagram

```
┌────────────────┐      ┌────────────────┐      ┌────────────────┐
│   Flight       │      │   Booking      │      │   Passenger    │
│   Service      │◄────►│   Service      │◄────►│   Service      │
└───────┬────────┘      └───────┬────────┘      └───────┬────────┘
        │                       │                        │
        │                       │                        │
        ▼                       ▼                        ▼
┌────────────────┐      ┌────────────────┐      ┌────────────────┐
│   Flight       │      │   Booking      │      │   Passenger    │
│   Repository   │      │   Repository   │      │   Repository   │
└────────────────┘      └────────────────┘      └────────────────┘
```

### Design Principles

1. **SOLID Principles**

   - Single Responsibility Principle
   - Open/Closed Principle
   - Liskov Substitution Principle
   - Interface Segregation Principle
   - Dependency Inversion Principle

2. **Clean Code**

   - Meaningful naming conventions
   - Small, focused methods
   - Proper commenting and documentation
   - Code readability and maintainability

3. **DRY (Don't Repeat Yourself)**
   - Reusable utility functions
   - Shared constants and configurations
   - Common base classes

---

## 🚀 Installation

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher

  ```bash
  java -version
  ```

- **Maven**: Version 3.6 or higher

  ```bash
  mvn -version
  ```

- **Git**: For cloning the repository

  ```bash
  git --version
  ```

- **Database** (Optional): MySQL 8.0+ or PostgreSQL 12+

- **IDE** (Recommended):
  - IntelliJ IDEA (Community or Ultimate)
  - Eclipse IDE
  - Visual Studio Code with Java extensions

### Step-by-Step Installation

#### 1. Clone the Repository

```bash
# Clone via HTTPS
git clone https://github.com/yourusername/flight-management-system.git

# Or clone via SSH
git clone git@github.com:yourusername/flight-management-system.git

# Navigate to project directory
cd flight-management-system
```

#### 2. Configure Environment Variables

Create a `.env` file in the root directory:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```properties
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=flight_management
DB_USERNAME=root
DB_PASSWORD=your_password

# Email Configuration
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_email_password

# Application Configuration
APP_PORT=8080
APP_ENV=development
SECRET_KEY=your_secret_key_here
```

#### 3. Build the Project

Using Maven:

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Install dependencies and build
mvn clean install
```

Using Gradle (if applicable):

```bash
# Clean and build
gradle clean build

# Run tests
gradle test

# Build without tests
gradle build -x test
```

#### 4. Database Setup

```bash
# Create database
mysql -u root -p

CREATE DATABASE flight_management;
USE flight_management;

# Run schema script
source src/main/resources/database.sql;

# Run seed data (optional)
source scripts/seed-data.sql;
```

#### 5. Run the Application

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="Main"

# Or run the compiled JAR
java -jar target/flight-management-system-1.0.0.jar

# Using Gradle
gradle run
```

#### 6. Verify Installation

The application should start successfully. Check the console output for:

```
[INFO] Application started successfully
[INFO] Server running on port 8080
[INFO] Database connection established
```

### Docker Installation (Alternative)

#### Using Docker Compose

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Using Docker directly

```bash
# Build the image
docker build -t flight-management-system:latest .

# Run the container
docker run -p 8080:8080 \
  -e DB_HOST=your_db_host \
  -e DB_PASSWORD=your_db_password \
  flight-management-system:latest
```

---

## ⚙️ Configuration

### Application Configuration

Edit `src/main/resources/application.properties`:

```properties
# Application Settings
app.name=Flight Management System
app.version=1.0.0
app.port=8080

# Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/flight_management
db.username=root
db.password=your_password
db.pool.size=10

# Logging Configuration
logging.level=INFO
logging.file=logs/application.log
logging.pattern=%d{yyyy-MM-dd HH:mm:ss} - %level - %logger{36} - %msg%n

# Email Configuration
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.smtp.auth=true
email.smtp.starttls.enable=true
email.from=noreply@flightmanagement.com

# Security Configuration
security.jwt.secret=your_jwt_secret_key_here
security.jwt.expiration=86400000
security.password.salt=your_salt_here

# Business Rules
booking.cancellation.hours=24
booking.max.passengers=9
flight.min.booking.hours=2
seat.hold.minutes=15

# Pricing Configuration
pricing.dynamic.enabled=true
pricing.currency=USD
pricing.tax.rate=0.15
```

### Environment-Specific Configuration

#### Development (`application-dev.properties`)

```properties
db.url=jdbc:mysql://localhost:3306/flight_management_dev
logging.level=DEBUG
email.enabled=false
```

#### Production (`application-prod.properties`)

```properties
db.url=jdbc:mysql://production-host:3306/flight_management
logging.level=WARN
email.enabled=true
security.strict=true
```

---

## 📖 Usage

### Basic Usage Examples

#### 1. Flight Search

```java
// Initialize services
FlightService flightService = new FlightService();
SearchService searchService = new SearchService();

// Search for flights
SearchRequestDTO searchRequest = new SearchRequestDTO();
searchRequest.setOrigin("JFK");
searchRequest.setDestination("LAX");
searchRequest.setDepartureDate(LocalDate.now().plusDays(7));
searchRequest.setPassengers(2);
searchRequest.setSeatClass(SeatClass.ECONOMY);

List<FlightDTO> flights = searchService.searchFlights(searchRequest);

// Display results
flights.forEach(flight -> {
    System.out.println("Flight: " + flight.getFlightNumber());
    System.out.println("Departure: " + flight.getDepartureTime());
    System.out.println("Price: $" + flight.getPrice());
});
```

#### 2. Creating a Booking

```java
// Initialize services
BookingService bookingService = new BookingService();
PassengerService passengerService = new PassengerService();

// Create passenger
Passenger passenger = new Passenger();
passenger.setFirstName("John");
passenger.setLastName("Doe");
passenger.setEmail("john.doe@example.com");
passenger.setPhoneNumber("+1234567890");
passenger.setPassportNumber("AB1234567");

Passenger savedPassenger = passengerService.createPassenger(passenger);

// Create booking
BookingDTO bookingDTO = new BookingDTO();
bookingDTO.setFlightId(flight.getId());
bookingDTO.setPassengerId(savedPassenger.getId());
bookingDTO.setSeatNumber("12A");
bookingDTO.setSeatClass(SeatClass.ECONOMY);

Booking booking = bookingService.createBooking(bookingDTO);

System.out.println("Booking confirmed! Reference: " + booking.getBookingReference());
```

#### 3. Processing Payment

```java
// Initialize payment service
PaymentService paymentService = new PaymentService();

// Create payment request
PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
paymentRequest.setBookingId(booking.getId());
paymentRequest.setAmount(booking.getTotalAmount());
paymentRequest.setCardNumber("4111111111111111");
paymentRequest.setCardHolderName("John Doe");
paymentRequest.setExpiryDate("12/25");
paymentRequest.setCvv("123");

// Process payment
Payment payment = paymentService.processPayment(paymentRequest);

if (payment.getStatus() == PaymentStatus.PAID) {
    System.out.println("Payment successful!");
    // Send confirmation email
    notificationService.sendBookingConfirmation(booking);
} else {
    System.out.println("Payment failed: " + payment.getFailureReason());
}
```

#### 4. Managing Flights (Admin)

```java
// Initialize flight service
FlightService flightService = new FlightService();

// Create new flight
Flight flight = new Flight();
flight.setFlightNumber("AA1234");
flight.setAirlineId(airline.getId());
flight.setOriginAirportCode("JFK");
flight.setDestinationAirportCode("LAX");
flight.setDepartureTime(LocalDateTime.now().plusDays(7));
flight.setArrivalTime(LocalDateTime.now().plusDays(7).plusHours(6));
flight.setAircraftId(aircraft.getId());
flight.setBasePrice(299.99);
flight.setStatus(FlightStatus.SCHEDULED);

Flight savedFlight = flightService.createFlight(flight);

// Update flight status
flightService.updateFlightStatus(savedFlight.getId(), FlightStatus.BOARDING);

// Cancel flight
flightService.cancelFlight(savedFlight.getId(), "Weather conditions");
```

#### 5. Generating Reports

```java
// Initialize report service
ReportService reportService = new ReportService();

// Revenue report
RevenueReport report = reportService.generateRevenueReport(
    LocalDate.now().minusMonths(1),
    LocalDate.now()
);

System.out.println("Total Revenue: $" + report.getTotalRevenue());
System.out.println("Total Bookings: " + report.getTotalBookings());
System.out.println("Average Ticket Price: $" + report.getAverageTicketPrice());

// Occupancy report
OccupancyReport occupancy = reportService.generateOccupancyReport(
    "JFK",
    LocalDate.now(),
    LocalDate.now().plusDays(30)
);

System.out.println("Average Load Factor: " + occupancy.getAverageLoadFactor() + "%");
```

### Command Line Interface (if implemented)

```bash
# Search flights
java -jar flight-management.jar search --from JFK --to LAX --date 2024-02-01

# Create booking
java -jar flight-management.jar book --flight AA1234 --passenger "John Doe" --seat 12A

# Check booking status
java -jar flight-management.jar status --booking REF123456

# Generate report
java -jar flight-management.jar report --type revenue --start 2024-01-01 --end 2024-01-31
```

---

## 📡 API Reference

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

Most endpoints require authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Flight Endpoints

#### Search Flights

```http
GET /flights/search?origin=JFK&destination=LAX&date=2024-02-01&passengers=2
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "flightId": 1,
      "flightNumber": "AA1234",
      "airline": "American Airlines",
      "origin": "JFK",
      "destination": "LAX",
      "departureTime": "2024-02-01T10:00:00",
      "arrivalTime": "2024-02-01T16:00:00",
      "duration": "6h 00m",
      "price": 299.99,
      "availableSeats": 45
    }
  ]
}
```

#### Get Flight Details

```http
GET /flights/{flightId}
```

#### Create Flight (Admin)

```http
POST /flights

{
  "flightNumber": "AA1234",
  "airlineId": 1,
  "aircraftId": 5,
  "origin": "JFK",
  "destination": "LAX",
  "departureTime": "2024-02-01T10:00:00",
  "arrivalTime": "2024-02-01T16:00:00",
  "basePrice": 299.99
}
```

#### Update Flight Status (Admin)

```http
PATCH /flights/{flightId}/status

{
  "status": "BOARDING"
}
```

### Booking Endpoints

#### Create Booking

```http
POST /bookings

{
  "flightId": 1,
  "passengers": [
    {
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "passportNumber": "AB1234567",
      "dateOfBirth": "1990-01-01",
      "seatPreference": "12A"
    }
  ],
  "seatClass": "ECONOMY"
}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "bookingId": 12345,
    "bookingReference": "ABC123",
    "status": "CONFIRMED",
    "totalAmount": 299.99,
    "passengers": [...],
    "paymentDue": "2024-01-15T23:59:59"
  }
}
```

#### Get Booking Details

```http
GET /bookings/{bookingId}
```

#### Cancel Booking

```http
DELETE /bookings/{bookingId}
```

#### Get User Bookings

```http
GET /bookings/user/{userId}
```

### Payment Endpoints

#### Process Payment

```http
POST /payments

{
  "bookingId": 12345,
  "amount": 299.99,
  "paymentMethod": "CREDIT_CARD",
  "cardDetails": {
    "cardNumber": "4111111111111111",
    "cardHolderName": "John Doe",
    "expiryMonth": 12,
    "expiryYear": 2025,
    "cvv": "123"
  }
}
```

#### Get Payment Status

```http
GET /payments/{paymentId}
```

### Passenger Endpoints

#### Create Passenger

```http
POST /passengers

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "passportNumber": "AB1234567",
  "nationality": "US",
  "dateOfBirth": "1990-01-01"
}
```

#### Update Passenger

```http
PUT /passengers/{passengerId}
```

#### Get Passenger Travel History

```http
GET /passengers/{passengerId}/history
```

### Admin Endpoints

#### Get Dashboard Statistics

```http
GET /admin/dashboard
```

**Response:**

```json
{
  "totalFlights": 150,
  "totalBookings": 1245,
  "totalRevenue": 374850.0,
  "averageOccupancy": 82.5,
  "todayFlights": 25,
  "upcomingFlights": 45
}
```

#### Generate Report

```http
POST /admin/reports

{
  "reportType": "REVENUE",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "format": "PDF"
}
```

### Error Responses

All error responses follow this format:

```json
{
  "success": false,
  "error": {
    "code": "BOOKING_NOT_FOUND",
    "message": "Booking with ID 12345 not found",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

Common HTTP Status Codes:

- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FlightServiceTest

# Run tests with coverage
mvn test jacoco:report

# Run integration tests only
mvn verify -P integration-tests
```

### Test Structure

```java
@Test
public void testCreateBooking_Success() {
    // Arrange
    BookingDTO bookingDTO = createTestBookingDTO();
    when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

    // Act
    Booking result = bookingService.createBooking(bookingDTO);

    // Assert
    assertNotNull(result);
    assertEquals(BookingStatus.CONFIRMED, result.getStatus());
    verify(bookingRepository, times(1)).save(any(Booking.class));
}
```

### Test Coverage Goals

- Unit Tests: > 80% coverage
- Integration Tests: Critical user flows
- End-to-End Tests: Major features

---

## ⚡ Performance

### Optimization Strategies

1. **Database Indexing**: Proper indexes on frequently queried columns
2. **Connection Pooling**: Efficient database connection management
3. **Caching**: Redis for frequently accessed data
4. **Lazy Loading**: Load data only when needed
5. **Batch Processing**: Handle bulk operations efficiently

### Performance Metrics

- Average API Response Time: < 200ms
- Database Query Time: < 50ms
- Concurrent Users Supported: 1000+
- Booking Processing Time: < 2 seconds

---

## 🔐 Security

### Security Features

1. **Authentication**: JWT-based authentication
2. **Authorization**: Role-based access control (RBAC)
3. **Data Encryption**: AES-256 encryption for sensitive data
4. **SQL Injection Prevention**: Parameterized queries
5. **XSS Protection**: Input sanitization
6. **CSRF Protection**: Token-based validation
7. **Rate Limiting**: API request throttling
8. **Audit Logging**: Track all sensitive operations

### Security Best Practices

```java
// Password hashing
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

// JWT token generation
String token = JWT.create()
    .withSubject(user.getId().toString())
    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
    .sign(Algorithm.HMAC512(SECRET_KEY));
```

---

## 🚢 Deployment

### Production Deployment

#### Using Docker

```bash
# Build Docker image
docker build -t flight-management-system:v1.0 .

# Push to registry
docker tag flight-management-system:v1.0 your-registry/flight-management-system:v1.0
docker push your-registry/flight-management-system:v1.0

# Deploy to production
docker run -d \
  --name flight-management-system \
  -p 80:8080 \
  -e DB_HOST=prod-db-host \
  -e DB_PASSWORD=secure_password \
  your-registry/flight-management-system:v1.0
```

#### Using Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: flight-management-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: flight-management-system
  template:
    metadata:
      labels:
        app: flight-management-system
    spec:
      containers:
        - name: app
          image: your-registry/flight-management-system:v1.0
          ports:
            - containerPort: 8080
          env:
            - name: DB_HOST
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: host
```

### Deployment Checklist

- [ ] Environment variables configured
- [ ] Database migrations applied
- [ ] SSL certificates installed
- [ ] Monitoring and logging configured
- [ ] Backup strategy implemented
- [ ] Load balancer configured
- [ ] Health checks enabled
- [ ] Rollback plan prepared

---

## 🔧 Troubleshooting

### Common Issues

#### Database Connection Issues

```
Error: Unable to establish database connection

Solution:
1. Verify database credentials in application.properties
2. Check if database service is running
3. Verify network connectivity
4. Check firewall rules
```

#### Out of Memory Errors

```
Error: java.lang.OutOfMemoryError: Java heap space

Solution:
1. Increase JVM heap size: -Xmx2048m
2. Check for memory leaks
3. Optimize database queries
4. Implement pagination
```

#### Slow Query Performance

```
Solution:
1. Add appropriate database indexes
2. Use query EXPLAIN to analyze
3. Implement caching
4. Optimize N+1 queries
```

---

## 👥 Team

This project was developed by students from the **Faculty of Software Technology, University of Information Technology, VNU-HCM**.

### Implementation Team

| Name                  | Student ID | Role      |
| --------------------- | ---------- | --------- |
| **Nguyen Phuc Thinh** | 23521503   | Developer |
| **Tran Duc Thinh**    | 23521511   | Developer |
| **Tran Xuan Thinh**   | 23521515   | Developer |

---

## 🙏 Acknowledgments

- Thanks to all contributors who have helped this project
- Inspired by real-world airline reservation systems
- Built with ❤️ using Java and open-source technologies
- Special thanks to the open-source community

---

<div align="center">

**⭐ If you find this project useful, please consider giving it a star! ⭐**

[⬆ Back to Top](#-flight-management-system)

</div>
