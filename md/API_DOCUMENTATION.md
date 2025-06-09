# Flight Management System - API Documentation

## Quick Reference

### Base URLs
- **Development**: `http://localhost:8080/api`
- **Demo Mode**: Authentication disabled (all endpoints public)
- **Account Types**: `1` = Customer, `2` = Employee
- **Ticket Status**: `1` = Paid, `2` = Unpaid, `3` = Cancelled

### Most Common Endpoints
```typescript
// Flight Search
GET /api/flights/search?departureAirportId=1&arrivalAirportId=2&departureDate=2024-12-01T10:00:00&passengerCount=2

// Get All Airports  
GET /api/airports

// Get All Ticket Classes
GET /api/ticket-classes

// Book Tickets
POST /api/tickets/book
{
  "flightId": 1,
  "customerId": 1, 
  "ticketClassId": 1,
  "passengers": [{"passengerName": "John Doe", "email": "john@example.com", "citizenId": "1234567890", "phoneNumber": "+1234567890"}],
  "totalFare": 299.99
}

// Account Registration
POST /api/accounts/register
{
  "accountName": "John Doe",
  "password": "password123", 
  "email": "john@example.com",
  "citizenId": "1234567890",
  "phoneNumber": "+1234567890",
  "accountType": 1
}

// Login
POST /api/accounts/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

## Table of Contents
- [Authentication & Authorization](#authentication--authorization)
- [Data Models](#data-models)
- [API Endpoints](#api-endpoints)
  - [Account Management](#account-management)
  - [Airport Management](#airport-management)
  - [Flight Management](#flight-management)
  - [Flight Ticket Class Management](#flight-ticket-class-management)
  - [Ticket Management](#ticket-management)
  - [Customer Management](#customer-management)
  - [Employee Management](#employee-management)
  - [Passenger Management](#passenger-management)
  - [Plane Management](#plane-management)
  - [Flight Detail Management](#flight-detail-management)
  - [Chat System](#chat-system)
  - [System Parameters](#system-parameters)
- [TypeScript Interfaces](#typescript-interfaces)
- [Error Handling](#error-handling)
- [Integration Examples](#integration-examples)

## Authentication & Authorization

**Security Note**: The current system has security disabled for demo purposes with `permitAll()` configuration. In production, implement proper JWT authentication.

### Demo Mode Configuration
- All endpoints are publicly accessible
- No authentication tokens required
- Account types: `1` (Customer), `2` (Employee)

## Data Models

### Core DTOs

#### AccountDto
```java
public class AccountDto {
    private Integer accountId;
    private String accountName;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer accountType; // 1: Customer, 2: Employee
    private String accountTypeName; // "Customer" or "Employee"
}
```

#### AirportDto
```java
public class AirportDto {
    private Integer airportId;
    private String airportName;
    private String cityName;
    private String countryName;
}
```

#### FlightDto
```java
public class FlightDto {
    private Integer flightId;
    private String flightCode;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer planeId;
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    
    // Display fields (populated by mapper)
    private String planeCode;
    private String departureAirportName;
    private String departureCityName;
    private String arrivalAirportName;
    private String arrivalCityName;
}
```

#### FlightTicketClassDto
```java
public class FlightTicketClassDto {
    private Integer flightId;
    private Integer ticketClassId;
    private String ticketClassName;
    private String color;
    private String flightCode;
    private Integer ticketQuantity;
    private Integer remainingTicketQuantity;
    private BigDecimal specifiedFare;
    private Boolean isAvailable; // true if remainingTicketQuantity > 0
}
```

#### TicketDto
```java
public class TicketDto {
    private Integer ticketId;
    private Integer flightId;
    private String flightCode;
    private Integer ticketClassId;
    private String ticketClassName;
    private Integer bookCustomerId;
    private Integer passengerId;
    private String passengerName;
    private String seatNumber;
    private Byte ticketStatus; // 1: Paid, 2: Unpaid, 3: Cancelled
    private LocalDateTime paymentTime;
    private BigDecimal fare;
}
```

#### CustomerDto
```java
public class CustomerDto {
    private Integer customerId;
    private String accountName;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer score; // Loyalty points
}
```

#### PassengerDto
```java
public class PassengerDto {
    private Integer passengerId;
    private String passengerName;
    private String email;
    private String citizenId;
    private String phoneNumber;
}
```

#### PlaneDto
```java
public class PlaneDto {
    private Integer planeId;
    private String planeCode;
    private String planeType;
    private Integer seatQuantity;
}
```

#### TicketClassDto
```java
public class TicketClassDto {
    private Integer ticketClassId;
    private String ticketClassName; // e.g., "Economy", "Business", "First"
    private String color; // CSS color for UI display
}
```

### Request/Response DTOs

#### RegisterDto
```java
public class RegisterDto {
    private String accountName;
    private String password;
    private String email;
    private String citizenId;
    private String phoneNumber;
    private Integer accountType; // 1: Customer, 2: Employee
}
```

#### LoginDto
```java
public class LoginDto {
    private String email;
    private String password;
}
```

#### LoginResponseDto
```java
public class LoginResponseDto {
    private Integer accountId;
    private String accountName;
    private String email;
    private Integer accountType;
    private String token; // Currently null in demo mode
}
```

#### FlightSearchCriteria
```java
public class FlightSearchCriteria {
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate; // Optional
    private Integer passengerCount;
    private Integer ticketClassId; // Optional
}
```

#### BookingDto
```java
public class BookingDto {
    private Integer flightId;
    private Integer customerId;
    private Integer ticketClassId;
    private List<PassengerDto> passengers;
    private BigDecimal totalFare;
    private List<String> seatNumbers; // Optional, system generates if not provided
}
```

## API Endpoints

### Account Management

#### POST /api/accounts/register
Register a new account (customer or employee).

**Request Body**: `RegisterDto`
```json
{
    "accountName": "John Doe",
    "password": "password123",
    "email": "john.doe@example.com",
    "citizenId": "1234567890",
    "phoneNumber": "+1234567890",
    "accountType": 1
}
```

**Response**: `AccountDto`
```json
{
    "accountId": 1,
    "accountName": "John Doe",
    "email": "john.doe@example.com",
    "citizenId": "1234567890",
    "phoneNumber": "+1234567890",
    "accountType": 1,
    "accountTypeName": "Customer"
}
```

#### POST /api/accounts/login
Authenticate user and get account information.

**Request Body**: `LoginDto`
```json
{
    "email": "john.doe@example.com",
    "password": "password123"
}
```

**Response**: `LoginResponseDto`
```json
{
    "accountId": 1,
    "accountName": "John Doe",
    "email": "john.doe@example.com",
    "accountType": 1,
    "token": null
}
```

#### GET /api/accounts
Get all accounts.

**Response**: `List<AccountDto>`

#### GET /api/accounts/{id}
Get account by ID.

**Response**: `AccountDto`

#### PUT /api/accounts/{id}
Update account information.

**Request Body**: `AccountDto`
**Response**: `AccountDto`

#### DELETE /api/accounts/{id}
Soft delete account.

**Response**: `204 No Content`

### Airport Management

#### GET /api/airports
Get all active airports.

**Response**: `List<AirportDto>`
```json
[
    {
        "airportId": 1,
        "airportName": "John F. Kennedy International Airport",
        "cityName": "New York",
        "countryName": "United States"
    }
]
```

#### GET /api/airports/{id}
Get airport by ID.

**Response**: `AirportDto`

#### POST /api/airports
Create new airport.

**Request Body**: `AirportDto`
**Response**: `AirportDto`

#### PUT /api/airports/{id}
Update airport information.

**Request Body**: `AirportDto`
**Response**: `AirportDto`

#### DELETE /api/airports/{id}
Soft delete airport.

**Response**: `204 No Content`

### Flight Management

#### GET /api/flights
Get all active flights.

**Response**: `List<FlightDto>`

#### GET /api/flights/{id}
Get flight by ID.

**Response**: `FlightDto`

#### GET /api/flights/code/{code}
Get flight by flight code.

**Parameters**:
- `code`: Flight code (e.g., "AA123")

**Response**: `FlightDto`

#### GET /api/flights/search
Search flights with criteria.

**Query Parameters**:
- `departureAirportId` (required): Departure airport ID
- `arrivalAirportId` (required): Arrival airport ID
- `departureDate` (required): Departure date in ISO format
- `returnDate` (optional): Return date in ISO format
- `passengerCount` (required): Number of passengers
- `ticketClassId` (optional): Specific ticket class ID

**Example**:
```
GET /api/flights/search?departureAirportId=1&arrivalAirportId=2&departureDate=2024-12-01T10:00:00&passengerCount=2&ticketClassId=1
```

**Response**: `List<FlightDto>`

#### GET /api/flights/route
Get flights by route and date.

**Query Parameters**:
- `departureAirportId`: Departure airport ID
- `arrivalAirportId`: Arrival airport ID
- `departureDate`: Departure date in ISO format

**Response**: `List<FlightDto>`

#### GET /api/flights/date-range
Get flights within date range.

**Query Parameters**:
- `startDate`: Start date in ISO format
- `endDate`: End date in ISO format

**Response**: `List<FlightDto>`

#### GET /api/flights/search/date
Search flights by specific date.

**Query Parameters**:
- `departureDate`: Date in YYYY-MM-DD format

**Response**: `List<FlightDto>`

#### GET /api/flights/{id}/availability
Check ticket availability for a flight.

**Response**: `List<FlightTicketClassDto>`

#### POST /api/flights
Create new flight.

**Request Body**: `FlightDto`
**Response**: `FlightDto`

#### PUT /api/flights/{id}
Update flight information.

**Request Body**: `FlightDto`
**Response**: `FlightDto`

#### DELETE /api/flights/{id}
Soft delete flight.

**Response**: `204 No Content`

### Flight Ticket Class Management

#### GET /api/flight-ticket-classes
Get all flight ticket class combinations.

**Response**: `List<FlightTicketClassDto>`

#### GET /api/flight-ticket-classes/{flightId}/{ticketClassId}
Get specific flight ticket class combination.

**Response**: `FlightTicketClassDto`

#### GET /api/flight-ticket-classes/flight/{flightId}
Get all ticket classes for a specific flight.

**Response**: `List<FlightTicketClassDto>`

#### GET /api/flight-ticket-classes/available
Get only available flight ticket classes (remaining tickets > 0).

**Response**: `List<FlightTicketClassDto>`

#### POST /api/flight-ticket-classes
Create flight ticket class combination.

**Request Body**: `FlightTicketClassDto`
**Response**: `FlightTicketClassDto`

#### PUT /api/flight-ticket-classes/{flightId}/{ticketClassId}
Update flight ticket class combination.

**Request Body**: `FlightTicketClassDto`
**Response**: `FlightTicketClassDto`

#### DELETE /api/flight-ticket-classes/{flightId}/{ticketClassId}
Delete flight ticket class combination.

**Response**: `204 No Content`

### Ticket Management

#### GET /api/tickets
Get all tickets.

**Response**: `List<TicketDto>`

#### GET /api/tickets/{id}
Get ticket by ID.

**Response**: `TicketDto`

#### GET /api/tickets/flight/{flightId}
Get all tickets for a specific flight.

**Response**: `List<TicketDto>`

#### GET /api/tickets/customer/{customerId}
Get all tickets booked by a specific customer.

**Response**: `List<TicketDto>`

#### GET /api/tickets/passenger/{passengerId}
Get all tickets for a specific passenger.

**Response**: `List<TicketDto>`

#### GET /api/tickets/status/{status}
Get tickets by status.

**Parameters**:
- `status`: Ticket status (1: Paid, 2: Unpaid, 3: Cancelled)

**Response**: `List<TicketDto>`

#### POST /api/tickets/book
Book multiple tickets for passengers.

**Request Body**: `BookingDto`
```json
{
    "flightId": 1,
    "customerId": 1,
    "ticketClassId": 1,
    "passengers": [
        {
            "passengerName": "John Doe",
            "email": "john@example.com",
            "citizenId": "1234567890",
            "phoneNumber": "+1234567890"
        }
    ],
    "totalFare": 299.99,
    "seatNumbers": ["12A"]
}
```

**Response**: `List<TicketDto>`

#### POST /api/tickets
Create single ticket.

**Request Body**: `TicketDto`
**Response**: `TicketDto`

#### PUT /api/tickets/{id}
Update ticket information.

**Request Body**: `TicketDto`
**Response**: `TicketDto`

#### PUT /api/tickets/{id}/pay
Mark ticket as paid.

**Response**: `TicketDto`

#### PUT /api/tickets/{id}/cancel
Cancel ticket.

**Response**: `204 No Content`

#### DELETE /api/tickets/{id}
Delete ticket.

**Response**: `204 No Content`

### Customer Management

#### GET /api/customers
Get all customers.

**Response**: `List<CustomerDto>`

#### GET /api/customers/{id}
Get customer by ID.

**Response**: `CustomerDto`

#### POST /api/customers
Create new customer.

**Request Body**: `CustomerDto`
**Response**: `CustomerDto`

#### PUT /api/customers/{id}
Update customer information.

**Request Body**: `CustomerDto`
**Response**: `CustomerDto`

#### DELETE /api/customers/{id}
Soft delete customer.

**Response**: `204 No Content`

### Employee Management

#### GET /api/employees
Get all employees.

**Response**: `List<EmployeeDto>`

#### GET /api/employees/{id}
Get employee by ID.

**Response**: `EmployeeDto`

#### POST /api/employees
Create new employee.

**Request Body**: `EmployeeDto`
**Response**: `EmployeeDto`

#### PUT /api/employees/{id}
Update employee information.

**Request Body**: `EmployeeDto`
**Response**: `EmployeeDto`

#### DELETE /api/employees/{id}
Soft delete employee.

**Response**: `204 No Content`

### Passenger Management

#### GET /api/passengers
Get all passengers.

**Response**: `List<PassengerDto>`

#### GET /api/passengers/{id}
Get passenger by ID.

**Response**: `PassengerDto`

#### GET /api/passengers/email/{email}
Get passengers by email.

**Response**: `List<PassengerDto>`

#### GET /api/passengers/search
Search passengers by name.

**Query Parameters**:
- `name`: Passenger name (partial match, case-insensitive)

**Response**: `List<PassengerDto>`

#### POST /api/passengers
Create new passenger.

**Request Body**: `PassengerDto`
**Response**: `PassengerDto`

#### PUT /api/passengers/{id}
Update passenger information.

**Request Body**: `PassengerDto`
**Response**: `PassengerDto`

#### DELETE /api/passengers/{id}
Delete passenger.

**Response**: `204 No Content`

### Plane Management

#### GET /api/planes
Get all active planes.

**Response**: `List<PlaneDto>`

#### GET /api/planes/{id}
Get plane by ID.

**Response**: `PlaneDto`

#### GET /api/planes/code/{code}
Get plane by plane code.

**Response**: `PlaneDto`

#### GET /api/planes/type/{type}
Get planes by type.

**Response**: `List<PlaneDto>`

#### POST /api/planes
Create new plane.

**Request Body**: `PlaneDto`
**Response**: `PlaneDto`

#### PUT /api/planes/{id}
Update plane information.

**Request Body**: `PlaneDto`
**Response**: `PlaneDto`

#### DELETE /api/planes/{id}
Soft delete plane.

**Response**: `204 No Content`

### Flight Detail Management

#### GET /api/flight-details
Get all flight details (layovers).

**Response**: `List<FlightDetailDto>`

#### GET /api/flight-details/flight/{flightId}
Get layover details for a specific flight.

**Response**: `List<FlightDetailDto>`

#### GET /api/flight-details/airport/{airportId}
Get all flight details involving a specific airport as layover.

**Response**: `List<FlightDetailDto>`

#### POST /api/flight-details
Create flight detail (layover).

**Request Body**: `FlightDetailDto`
**Response**: `FlightDetailDto`

#### PUT /api/flight-details/{flightId}/{mediumAirportId}
Update flight detail.

**Request Body**: `FlightDetailDto`
**Response**: `FlightDetailDto`

#### DELETE /api/flight-details/{flightId}/{mediumAirportId}
Delete flight detail.

**Response**: `204 No Content`

### Ticket Class Management

#### GET /api/ticket-classes
Get all ticket classes.

**Response**: `List<TicketClassDto>`

#### GET /api/ticket-classes/{id}
Get ticket class by ID.

**Response**: `TicketClassDto`

#### POST /api/ticket-classes
Create new ticket class.

**Request Body**: `TicketClassDto`
**Response**: `TicketClassDto`

#### PUT /api/ticket-classes/{id}
Update ticket class.

**Request Body**: `TicketClassDto`
**Response**: `TicketClassDto`

#### DELETE /api/ticket-classes/{id}
Delete ticket class.

**Response**: `204 No Content`

### Chat System

#### GET /api/chatboxes
Get all chatboxes.

**Response**: `List<ChatboxDto>`

#### GET /api/chatboxes/customer/{customerId}/chatbox
Get or create chatbox for a specific customer.

**Response**: `ChatboxDto`

#### POST /api/chatboxes
Create new chatbox.

**Request Body**: `ChatboxDto`
**Response**: `ChatboxDto`

#### GET /api/messages/chatbox/{chatboxId}
Get all messages in a chatbox.

**Response**: `List<MessageDto>`

#### POST /api/messages/customer
Send customer message.

**Request Body**: `{ "chatboxId": number, "content": string }`
**Response**: `MessageDto`

#### POST /api/messages/employee
Send employee message.

**Request Body**: `{ "chatboxId": number, "content": string }`
**Response**: `MessageDto`

### System Parameters

#### GET /api/parameters
Get current system parameters.

**Response**: `ParameterDto`
```json
{
    "id": 1,
    "maxMediumAirport": 3,
    "minFlightDuration": 30,
    "minLayoverDuration": 30,
    "maxLayoverDuration": 1440,
    "minBookingInAdvanceDuration": 60,
    "maxBookingHoldDuration": 15
}
```

#### PUT /api/parameters
Update system parameters.

**Request Body**: `ParameterDto`
**Response**: `ParameterDto`

## TypeScript Interfaces

For frontend integration, use these TypeScript interfaces:

```typescript
// Authentication
interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  accountId: number;
  accountName: string;
  email: string;
  accountType: number;
  token?: string;
}

interface RegisterRequest {
  accountName: string;
  password: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
  accountType: number;
}

// Core Models
interface Account {
  accountId?: number;
  accountName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
  accountType: number;
  accountTypeName?: string;
}

interface Airport {
  airportId?: number;
  airportName: string;
  cityName: string;
  countryName: string;
}

interface Flight {
  flightId?: number;
  flightCode: string;
  departureTime: string; // ISO string
  arrivalTime: string; // ISO string
  planeId: number;
  departureAirportId: number;
  arrivalAirportId: number;
  
  // Display fields
  planeCode?: string;
  departureAirportName?: string;
  departureCityName?: string;
  arrivalAirportName?: string;
  arrivalCityName?: string;
}

interface FlightTicketClass {
  flightId: number;
  ticketClassId: number;
  ticketClassName?: string;
  color?: string;
  flightCode?: string;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
  isAvailable?: boolean;
}

interface Ticket {
  ticketId?: number;
  flightId: number;
  flightCode?: string;
  ticketClassId: number;
  ticketClassName?: string;
  bookCustomerId: number;
  passengerId: number;
  passengerName?: string;
  seatNumber: string;
  ticketStatus: number; // 1: Paid, 2: Unpaid, 3: Cancelled
  paymentTime?: string; // ISO string
  fare: number;
}

interface Customer {
  customerId?: number;
  accountName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
  score: number;
}

interface Passenger {
  passengerId?: number;
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

interface Plane {
  planeId?: number;
  planeCode: string;
  planeType: string;
  seatQuantity: number;
}

interface TicketClass {
  ticketClassId?: number;
  ticketClassName: string;
  color: string;
}

// Search and Booking
interface FlightSearchCriteria {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string; // ISO string
  returnDate?: string; // ISO string
  passengerCount: number;
  ticketClassId?: number;
}

interface BookingRequest {
  flightId: number;
  customerId: number;
  ticketClassId: number;
  passengers: Passenger[];
  totalFare: number;
  seatNumbers?: string[];
}

// Chat System
interface Chatbox {
  chatboxId?: number;
  customerId: number;
  customerName?: string;
  employeeId: number;
  employeeName?: string;
  lastMessageTime?: string; // ISO string
  lastMessageContent?: string;
  unreadCount?: number;
}

interface Message {
  messageId?: number;
  chatboxId: number;
  messageType: number; // 1: customer to employee, 2: employee to customer
  content: string;
  sendTime?: string; // ISO string
  senderName?: string;
  isFromCustomer?: boolean;
}

// System Configuration
interface SystemParameters {
  id?: number;
  maxMediumAirport: number;
  minFlightDuration: number;
  minLayoverDuration: number;
  maxLayoverDuration: number;
  minBookingInAdvanceDuration: number;
  maxBookingHoldDuration: number;
}
```

## Error Handling

### HTTP Status Codes

- **200 OK**: Successful GET, PUT requests
- **201 Created**: Successful POST requests
- **204 No Content**: Successful DELETE requests
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Error Response Format

```json
{
  "error": "Error type",
  "message": "Detailed error message"
}
```

### Common Error Scenarios

1. **Validation Errors**: Missing required fields, invalid data format
2. **Business Logic Errors**: 
   - Flight duration too short
   - Insufficient ticket availability
   - Invalid flight route (same departure/arrival airport)
3. **Resource Not Found**: Invalid IDs in path parameters
4. **Booking Conflicts**: Seat already taken, flight fully booked

## Integration Examples

### Flight Search and Booking Flow

```typescript
// 1. Search for flights
const searchCriteria: FlightSearchCriteria = {
  departureAirportId: 1,
  arrivalAirportId: 2,
  departureDate: '2024-12-01T10:00:00',
  passengerCount: 2,
  ticketClassId: 1
};

const flights = await fetch('/api/flights/search?' + new URLSearchParams({
  departureAirportId: searchCriteria.departureAirportId.toString(),
  arrivalAirportId: searchCriteria.arrivalAirportId.toString(),
  departureDate: searchCriteria.departureDate,
  passengerCount: searchCriteria.passengerCount.toString(),
  ticketClassId: searchCriteria.ticketClassId?.toString() || ''
})).then(res => res.json());

// 2. Check availability for selected flight
const availability = await fetch(`/api/flights/${selectedFlightId}/availability`)
  .then(res => res.json());

// 3. Book tickets
const bookingRequest: BookingRequest = {
  flightId: selectedFlightId,
  customerId: currentCustomerId,
  ticketClassId: selectedTicketClassId,
  passengers: [
    {
      passengerName: "John Doe",
      email: "john@example.com",
      citizenId: "1234567890",
      phoneNumber: "+1234567890"
    }
  ],
  totalFare: 299.99
};

const bookedTickets = await fetch('/api/tickets/book', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(bookingRequest)
}).then(res => res.json());

// 4. Pay for tickets
for (const ticket of bookedTickets) {
  await fetch(`/api/tickets/${ticket.ticketId}/pay`, { method: 'PUT' });
}
```

### Account Registration and Login

```typescript
// Register new customer
const registerData: RegisterRequest = {
  accountName: "John Doe",
  password: "password123",
  email: "john@example.com",
  citizenId: "1234567890",
  phoneNumber: "+1234567890",
  accountType: 1 // Customer
};

const account = await fetch('/api/accounts/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(registerData)
}).then(res => res.json());

// Login
const loginData: LoginRequest = {
  email: "john@example.com",
  password: "password123"
};

const loginResponse = await fetch('/api/accounts/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(loginData)
}).then(res => res.json());
```

### Data Loading for UI

```typescript
// Load reference data for forms
const [airports, ticketClasses, planes] = await Promise.all([
  fetch('/api/airports').then(res => res.json()),
  fetch('/api/ticket-classes').then(res => res.json()),
  fetch('/api/planes').then(res => res.json())
]);

// Get customer's booking history
const customerTickets = await fetch(`/api/tickets/customer/${customerId}`)
  .then(res => res.json());
```

## Base URL Configuration

- **Development**: `http://localhost:8080`
- **Frontend Dev Server**: `http://localhost:3000` or `http://localhost:5173`
- **CORS**: Enabled for frontend origins

## Validation Rules

### Backend Validation Requirements

#### Account Registration
- **accountName**: 3-50 characters, non-null
- **email**: Valid email format, unique in system
- **password**: Minimum 6 characters (frontend requirement)
- **citizenId**: 10-12 digits, unique in system
- **phoneNumber**: Valid phone format (E.164 recommended)
- **accountType**: Must be 1 (Customer) or 2 (Employee)

#### Flight Management
- **flightCode**: 2-8 characters, uppercase alphanumeric
- **departureTime/arrivalTime**: Must be future dates, arrival > departure
- **Flight Duration**: Minimum duration based on system parameters
- **Route Validation**: Departure and arrival airports must be different

#### Passenger Information
- **passengerName**: 2-100 characters, non-null
- **email**: Valid email format
- **citizenId**: 10-12 digits
- **phoneNumber**: Valid phone format

#### Booking Rules
- **Advance Booking**: Must be booked at least minBookingInAdvanceDuration minutes before departure
- **Seat Availability**: Cannot book more tickets than available
- **Passenger Limit**: Maximum passengers per booking based on plane capacity

## Advanced API Features

### Pagination Support
Most list endpoints support pagination parameters:

```
GET /api/flights?page=0&size=20&sort=departureTime,asc
```

**Parameters**:
- `page`: Page number (0-based)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Sort criteria (property,direction)

### Flight Search Advanced Features

#### Multi-City Search
```typescript
// Search with multiple criteria
const complexSearch = {
  routes: [
    { departureAirportId: 1, arrivalAirportId: 2, departureDate: '2024-12-01' },
    { departureAirportId: 2, arrivalAirportId: 3, departureDate: '2024-12-05' }
  ],
  passengerCount: 2,
  flexible: true // +/- 3 days from specified dates
};
```

#### Price Range Filtering
```
GET /api/flights/search?minFare=100&maxFare=500&departureAirportId=1&arrivalAirportId=2
```

#### Availability-Only Search
```
GET /api/flights/search?availableOnly=true&passengerCount=2
```

### Real-time Features

#### WebSocket Endpoints (Future Enhancement)
- `/ws/chat/{chatboxId}`: Real-time chat messages
- `/ws/flights/{flightId}/availability`: Live seat availability updates
- `/ws/notifications/{customerId}`: Booking confirmations, flight updates

## Error Handling Examples

### Validation Error Response
```json
{
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": {
    "email": "Email format is invalid",
    "citizenId": "Citizen ID must be 10-12 digits",
    "phoneNumber": "Phone number format is invalid"
  }
}
```

### Business Logic Error Response
```json
{
  "error": "Booking Not Allowed",
  "message": "Flight must be booked at least 60 minutes in advance",
  "code": "BOOKING_TOO_LATE",
  "flightId": 123
}
```

### Resource Conflict Error
```json
{
  "error": "Seat Unavailable",
  "message": "Seat 12A is already taken",
  "code": "SEAT_CONFLICT",
  "availableSeats": ["12B", "12C", "13A"]
}
```

## Performance Considerations

### Caching Strategy
- **Static Data**: Airport, TicketClass, Plane data cached for 1 hour
- **Flight Data**: Cached for 5 minutes due to availability changes
- **User Data**: No caching for personalized information

### Rate Limiting
- **Search Operations**: 60 requests per minute per IP
- **Booking Operations**: 10 requests per minute per user
- **General API**: 100 requests per minute per IP

### Optimized Endpoints

#### Bulk Operations
```typescript
// Get multiple flights with availability in one call
GET /api/flights/bulk-availability?flightIds=1,2,3,4,5

// Bulk seat assignment
POST /api/tickets/bulk-assign-seats
{
  "assignments": [
    { "ticketId": 1, "seatNumber": "12A" },
    { "ticketId": 2, "seatNumber": "12B" }
  ]
}
```

## Security Implementation Guide

### When JWT is Enabled

#### Request Headers
```typescript
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};
```

#### Token Refresh
```typescript
// Automatic token refresh before expiration
const refreshToken = async () => {
  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${currentToken}` }
  });
  return response.json();
};
```

#### Role-Based Access
- **Customer Routes**: `/api/tickets/customer/*`, `/api/chatboxes/customer/*`
- **Employee Routes**: `/api/employees/*`, `/api/flights/manage/*`
- **Admin Routes**: `/api/accounts/*`, `/api/parameters/*`

### Security Headers
```typescript
// Required headers for production
const securityHeaders = {
  'X-Content-Type-Options': 'nosniff',
  'X-Frame-Options': 'DENY',
  'X-XSS-Protection': '1; mode=block'
};
```

## Testing and Development

### Mock Data Generators
```typescript
// Generate test data for development
const mockFlight = (): Flight => ({
  flightCode: `AA${Math.floor(Math.random() * 9999)}`,
  departureTime: new Date(Date.now() + 86400000).toISOString(),
  arrivalTime: new Date(Date.now() + 86400000 + 7200000).toISOString(),
  planeId: 1,
  departureAirportId: 1,
  arrivalAirportId: 2
});
```

### Health Check Endpoint
```
GET /api/health
```
Response:
```json
{
  "status": "UP",
  "database": "CONNECTED",
  "version": "1.0.0",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Migration and Deployment

### Database Schema Version
Current schema version: `1.0.0` (matches `fms_db.sql`)

### Environment Configuration
```env
# Backend Configuration
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/fms
DB_USERNAME=root
DB_PASSWORD=password

# Frontend Configuration  
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_WS_BASE_URL=ws://localhost:8080/ws
```

### Production Deployment Checklist
1. ✅ Enable JWT authentication in SecurityConfig
2. ✅ Configure CORS for production domains
3. ✅ Set up SSL/TLS certificates
4. ✅ Configure database connection pooling
5. ✅ Enable request logging and monitoring
6. ✅ Set up automated backups
7. ✅ Configure rate limiting
8. ✅ Test all API endpoints with production data

## Notes for Frontend Integration

1. **Date Formats**: Use ISO 8601 format for all date/time fields
2. **Numeric IDs**: All ID fields are integers
3. **Optional Fields**: Check for null/undefined values in display fields
4. **Error Handling**: Always handle HTTP error status codes and validate response structure
5. **Loading States**: API calls may take time, implement loading indicators
6. **Validation**: Implement client-side validation matching backend rules
7. **Security**: Currently in demo mode - implement proper authentication for production
8. **Performance**: Use pagination for large datasets and implement optimistic updates
9. **Real-time**: Plan for WebSocket integration for chat and live updates
10. **Accessibility**: Ensure API error messages are user-friendly and translatable

## API Client Library Example

```typescript
// Centralized API client for type safety
class FlightManagementAPI {
  private baseURL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
  
  async searchFlights(criteria: FlightSearchCriteria): Promise<Flight[]> {
    const params = new URLSearchParams();
    Object.entries(criteria).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params.append(key, value.toString());
      }
    });
    
    const response = await fetch(`${this.baseURL}/flights/search?${params}`);
    if (!response.ok) throw new Error(`Search failed: ${response.statusText}`);
    return response.json();
  }
  
  async bookTickets(booking: BookingRequest): Promise<Ticket[]> {
    const response = await fetch(`${this.baseURL}/tickets/book`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(booking)
    });
    if (!response.ok) throw new Error(`Booking failed: ${response.statusText}`);
    return response.json();
  }
}

export const api = new FlightManagementAPI();
```

This comprehensive API documentation provides complete specifications for frontend integration with the Flight Management System backend, including validation rules, advanced features, security considerations, and practical examples for development teams.
