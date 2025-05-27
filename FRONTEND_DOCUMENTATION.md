# Flight Management System - Frontend Documentation

## Table of Contents
1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Architecture](#architecture)
5. [Components](#components)
6. [Services & API Integration](#services--api-integration)
7. [Authentication & Authorization](#authentication--authorization)
8. [Routing System](#routing-system)
9. [State Management](#state-management)
10. [Models & Types](#models--types)
11. [Styling & UI](#styling--ui)
12. [Build & Deployment](#build--deployment)
13. [Development Guidelines](#development-guidelines)

## Overview

The Flight Management System frontend is a modern React-based single-page application (SPA) built with TypeScript. It provides a comprehensive interface for flight booking, administration, and customer management with role-based access control.

### Key Features
- **Multi-role Support**: Customer (accountType: 1) and Employee (accountType: 2) interfaces
- **Flight Search & Booking**: Real-time flight search with booking capabilities
- **Admin Panel**: Complete CRUD operations for system entities
- **Customer Dashboard**: Ticket management and booking history
- **Real-time Chat**: Customer support integration (planned)
- **Responsive Design**: Mobile-first approach with modern UI

### Account Type Mapping (CRITICAL FIX REQUIRED)
**Current Issue**: Frontend-Backend Mapping Inconsistency

**Database Schema (Correct)**:
- `accountType: 1` = Customer (access to booking and personal ticket management)
- `accountType: 2` = Employee (access to admin panels and management functions)

**Frontend Implementation (INCORRECT)**:
- Frontend expects `accountType: 1` for admin/employee
- Frontend expects `accountType: 2` for customer

**Required Fix**: Update frontend `usePermissions` hook to match database schema

### Employee Types (for accountType = 2)
1. **Flight Schedule Reception** (tiep nhan lich bay) - Manage flight schedules
2. **Ticket Sales/Booking** (ban/dat ve) - Handle reservations and sales  
3. **Customer Service** (cskh) - Support and chat
4. **Accounting** (ke toan) - Financial operations
5. **System Administrator** (sa) - Full system access

## Technology Stack

### Core Technologies
- **React 19.0.0**: Modern React with hooks and functional components
- **TypeScript 5.6.2**: Type-safe JavaScript with strict typing
- **Vite 5.4.10**: Fast build tool and development server
- **React Router DOM 6.28.0**: Client-side routing with protected routes

### Development Tools
- **ESLint 9.13.0**: Code linting with TypeScript support
- **React Hook Form 7.53.2**: Efficient form handling and validation
- **Axios 1.7.9**: HTTP client for API communication
- **Vitest 2.1.8**: Modern testing framework
- **CSS3**: Modern styling with Flexbox and Grid

### Build Configuration
```javascript
// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

## Project Structure

```
frontend/
├── public/                          # Static assets
│   └── vite.svg                    # Vite logo
├── src/
│   ├── App.tsx                     # Main application component
│   ├── main.tsx                    # Application entry point (renamed from index.tsx)
│   ├── App.css                     # Global application styles
│   ├── index.css                   # Global base styles
│   │
│   ├── components/                 # React components
│   │   ├── admin/                  # Admin panel components
│   │   │   ├── AdminPanel.tsx      # Main admin dashboard
│   │   │   ├── AirportManagement.tsx
│   │   │   ├── EmployeeManagement.tsx
│   │   │   ├── FlightManagement.tsx
│   │   │   ├── FlightTicketClassManagement.tsx
│   │   │   ├── ParameterSettings.tsx
│   │   │   ├── TicketClassManagement.tsx    # Manage ticket classes
│   │   │   └── PlaneManagement.tsx         # Aircraft fleet management
│   │   │
│   │   ├── auth/                   # Authentication components
│   │   │   ├── LoginForm.tsx       # User login form
│   │   │   └── RegisterForm.tsx    # User registration form
│   │   │
│   │   ├── booking/                # Flight booking components
│   │   │   └── BookingForm.tsx     # Multi-step booking form
│   │   │
│   │   ├── chat/                   # Customer support chat
│   │   │   └── ChatWidget.tsx      # Real-time chat interface
│   │   │
│   │   ├── dashboard/              # User dashboard
│   │   │   └── Dashboard.tsx       # Customer dashboard
│   │   │
│   │   ├── flights/                # Flight-related components
│   │   │   ├── FlightCard.tsx      # Individual flight display
│   │   │   ├── FlightList.tsx      # Flight listing component
│   │   │   └── FlightSearch.tsx    # Flight search interface
│   │   │
│   │   ├── home/                   # Landing page components
│   │   ├── layout/                 # Layout components
│   │   ├── routes/                 # Route protection components
│   │   └── tickets/                # Ticket management components
│   │
│   ├── hooks/                      # Custom React hooks
│   │   └── useAuth.tsx             # Authentication context & hooks
│   │
│   ├── models/                     # TypeScript type definitions
│   │   ├── Account.ts              # User account types
│   │   ├── Airport.ts              # Airport data types
│   │   ├── Chat.ts                 # Chat message types
│   │   ├── Customer.ts             # Customer entity types
│   │   ├── Employee.ts             # Employee entity types
│   │   ├── Flight.ts               # Flight data types
│   │   ├── FlightDetail.ts         # Layover flight information
│   │   ├── FlightTicketClass.ts    # Flight-ticket class associations
│   │   ├── LoginResponse.ts        # Authentication response
│   │   ├── Parameter.ts            # System parameter types
│   │   ├── Passenger.ts            # Passenger data types
│   │   ├── Plane.ts                # Aircraft types
│   │   ├── Ticket.ts               # Ticket entity types
│   │   ├── TicketClass.ts          # Ticket class definitions
│   │   └── index.ts                # Type exports
│   │
│   ├── services/                   # API service layer
│   │   ├── api.ts                  # Base API client configuration
│   │   ├── config.ts               # Service configuration
│   │   ├── accountService.ts       # Account management
│   │   ├── airportService.ts       # Airport operations
│   │   ├── chatService.ts          # Chat functionality
│   │   ├── customerService.ts      # Customer operations
│   │   ├── demoService.ts          # Demo/test utilities
│   │   ├── employeeService.ts      # Employee management
│   │   ├── flightDetailService.ts  # Flight detail operations
│   │   ├── flightService.ts        # Flight operations
│   │   ├── flightTicketClassService.ts # ⚠️ MISSING - needs implementation
│   │   ├── messageService.ts       # Message handling
│   │   ├── parameterService.ts     # System parameters
│   │   ├── passengerService.ts     # ⚠️ MISSING - needs implementation
│   │   ├── planeService.ts         # Aircraft management
│   │   ├── ticketClassService.ts   # Ticket class operations
│   │   ├── ticketService.ts        # Ticket booking & management
│   │   └── index.ts                # Service exports
│   │
│   ├── test/                       # Test configuration and utilities
│   │   └── setup.ts                # Test setup with mocks
│   │
│   └── styles/                     # Additional stylesheets
│       └── FlightForm.css          # Flight form specific styles
│
├── package.json                    # Dependencies and scripts
├── vite.config.ts                  # Vite configuration
├── tsconfig.json                   # TypeScript configuration
├── eslint.config.js                # ESLint configuration
└── index.html                      # HTML template
```

## Architecture

### Component Architecture
The application follows a modular component architecture with clear separation of concerns:

```
App.tsx (Root)
├── AuthProvider (Context)
├── Router (react-router-dom)
├── Layout Components
└── Feature Components
    ├── Customer (accountType: 1 per DB schema)
    ├── Employee/Admin (accountType: 2 per DB schema)
    └── Public (Accessible to all)
```

### Data Flow
1. **Components** → Request data through **Services**
2. **Services** → Make HTTP calls via **API Client**
3. **API Client** → Communicates with **Backend REST APIs**
4. **Response** → Flows back through the same chain with type safety

### Layer Separation
- **Presentation Layer**: React components with TypeScript
- **Service Layer**: API abstraction and business logic
- **Model Layer**: TypeScript interfaces and types
- **Infrastructure**: HTTP client, routing, and authentication

## Components

### Admin Components

#### AdminPanel.tsx
Main administrative dashboard with role-based access control.

**Current Issue**: Role checking logic needs fixing for database schema alignment
```typescript
// Current logic (INCORRECT based on DB schema)
const { user } = useAuth();
const isAdmin = user?.accountType === 1; // This should be 2 per DB schema

// Should be:
const isAdmin = user?.accountType === 2; // Employee per database
```
#### AirportManagement.tsx
Complete CRUD operations for airport entities.

**Key Functions:**
- Create new airports with validation
- Update existing airport information
- Delete airports with confirmation
- Search and filter airports

#### FlightManagement.tsx
Comprehensive flight management interface.

**Capabilities:**
- Create and schedule new flights
- Modify flight details (time, route, aircraft)
- Cancel or reschedule flights
- View flight passenger manifests

#### EmployeeManagement.tsx
Employee administration and account management.

**Features:**
- Employee registration and onboarding
- Role assignment and permissions
- Employee profile management
- Account activation/deactivation

#### TicketClassManagement.tsx ⚠️ **NEEDS IMPLEMENTATION**
Admin interface for managing ticket classes (Economy, Business, First, etc.).

**Missing Features:**
- Create, update, delete ticket classes
- Assign display colors for UI
- Used in flight ticket class assignment

**Required API Integration:**
```typescript
interface TicketClassService {
  getAllTicketClasses(): Promise<TicketClass[]>;
  getTicketClassById(id: number): Promise<TicketClass>;
  createTicketClass(data: CreateTicketClassRequest): Promise<TicketClass>;
  updateTicketClass(id: number, data: UpdateTicketClassRequest): Promise<TicketClass>;
  deleteTicketClass(id: number): Promise<void>;
  getTicketClassByName(name: string): Promise<TicketClass>;
}
```

#### PlaneManagement.tsx ⚠️ **NEEDS IMPLEMENTATION**
Admin interface for managing aircraft fleet.

**Missing Features:**
- Create, update, delete planes
- View seat capacity and aircraft type
- Used in flight creation and scheduling

**Required API Integration:**
```typescript
interface PlaneService {
  getAllPlanes(): Promise<Plane[]>;
  getPlaneById(id: number): Promise<Plane>;
  createPlane(data: CreatePlaneRequest): Promise<Plane>;
  updatePlane(id: number, data: UpdatePlaneRequest): Promise<Plane>;
  deletePlane(id: number): Promise<void>;
  getPlaneByCode(code: string): Promise<Plane>;
  getPlanesByType(type: string): Promise<Plane[]>;
}
```

#### FlightTicketClassManagement.tsx ⚠️ **CRITICAL MISSING**
Junction entity management for flight-ticket class associations.

**Missing Features:**
- Assign ticket classes to specific flights
- Set pricing for each flight/class combination
- Manage seat availability per class
- Critical for booking system functionality

**Required Implementation:**
```typescript
interface FlightTicketClassService {
  getFlightTicketClassesByFlightId(flightId: number): Promise<FlightTicketClass[]>;
  getFlightTicketClassById(flightId: number, ticketClassId: number): Promise<FlightTicketClass>;
  createFlightTicketClass(data: CreateFlightTicketClassRequest): Promise<FlightTicketClass>;
  updateFlightTicketClass(flightId: number, ticketClassId: number, data: UpdateFlightTicketClassRequest): Promise<FlightTicketClass>;
  deleteFlightTicketClass(flightId: number, ticketClassId: number): Promise<void>;
  updateRemainingTickets(flightId: number, ticketClassId: number, quantity: number): Promise<void>;
  getAvailableFlightTicketClasses(): Promise<FlightTicketClass[]>;
}
```

### Authentication Components

#### LoginForm.tsx
User authentication interface with form validation.

**Current Implementation Issue**:
```typescript
// Frontend login expects these account types (INCORRECT per DB)
// accountType: 1 = admin/employee (should be customer per DB)
// accountType: 2 = customer (should be employee per DB)

const redirectAfterLogin = (accountType: number) => {
  if (accountType === 1) {
    navigate('/admin'); // Actually goes to customer per DB
  } else {
    navigate('/dashboard'); // Actually goes to employee per DB
  }
};
```

#### RegisterForm.tsx
Customer registration form with validation.

**Issue**: Registration may create accounts with wrong accountType values

### Booking Components

#### BookingForm.tsx ⚠️ **DATA TRANSFORMATION ISSUES**
Multi-step flight booking process with passenger management.

**Current Data Structure Issues**:
```typescript
// Frontend expects firstName/lastName separation
interface PassengerData {
  firstName: string;
  lastName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

// Backend expects combined passengerName
interface PassengerRequest {
  passengerName: string; // Combined from firstName + lastName
  email: string;
  citizenId: string;
  phoneNumber: string;
}
```

**Required Fix**:
```typescript
// In ticketService.bookTickets()
const transformedBooking = {
  ...booking,
  passengers: booking.passengers.map(p => ({
    passengerName: `${p.firstName} ${p.lastName}`, // ✅ Transformation needed
    email: p.email,
    citizenId: p.citizenId,
    phoneNumber: p.phoneNumber,
  }))
};
```

### Flight Components

#### FlightSearch.tsx ⚠️ **API INTEGRATION ISSUES**
Advanced flight search with filters and sorting.

**Current Issues**:
1. **Search parameter mismatch**: Frontend search criteria don't match backend expectations
2. **Missing FlightTicketClass integration**: Can't properly check class availability
3. **Error handling**: 500 errors on search due to missing services

**Required Fixes**:
```typescript
// Fix search criteria interface
interface FlightSearchCriteria {
  departureAirportId: number;    // ✅ Matches backend
  arrivalAirportId: number;      // ✅ Matches backend  
  departureDate: string;         // ✅ Matches backend
  returnDate?: string;           // ✅ Optional return flight
  passengerCount: number;        // ✅ Matches backend
  ticketClassId?: number;        // ⚠️ May cause 500 errors if not handled properly
}

// Integration with FlightTicketClass service
const checkAvailability = async (flightId: number) => {
  const availability = await flightTicketClassService.getFlightTicketClassesByFlightId(flightId);
  return availability.filter(ftc => ftc.remainingTicketQuantity > 0);
};
```

## Services & API Integration

### Missing Service Implementations

#### flightTicketClassService.ts ⚠️ **CRITICAL MISSING**
```typescript
// filepath: d:\flight-management-system\frontend\src\services\flightTicketClassService.ts
import { apiClient } from './api';

export interface FlightTicketClass {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
  
  // Extended properties from joins
  ticketClassName?: string;
  color?: string;
  flightCode?: string;
  isAvailable?: boolean;
  
  // Navigation properties
  flight?: Flight;
  ticketClass?: TicketClass;
}

export interface CreateFlightTicketClassRequest {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
}

export interface UpdateFlightTicketClassRequest {
  ticketQuantity?: number;
  remainingTicketQuantity?: number;
  specifiedFare?: number;
}

class FlightTicketClassService {
  async getAllFlightTicketClasses(): Promise<FlightTicketClass[]> {
    return apiClient.get('/flight-ticket-classes');
  }

  async getFlightTicketClassById(flightId: number, ticketClassId: number): Promise<FlightTicketClass> {
    return apiClient.get(`/flight-ticket-classes/${flightId}/${ticketClassId}`);
  }

  async getFlightTicketClassesByFlightId(flightId: number): Promise<FlightTicketClass[]> {
    return apiClient.get(`/flight-ticket-classes/flight/${flightId}`);
  }

  async createFlightTicketClass(data: CreateFlightTicketClassRequest): Promise<FlightTicketClass> {
    return apiClient.post('/flight-ticket-classes', data);
  }

  async updateFlightTicketClass(flightId: number, ticketClassId: number, data: UpdateFlightTicketClassRequest): Promise<FlightTicketClass> {
    return apiClient.put(`/flight-ticket-classes/${flightId}/${ticketClassId}`, data);
  }

  async deleteFlightTicketClass(flightId: number, ticketClassId: number): Promise<void> {
    return apiClient.delete(`/flight-ticket-classes/${flightId}/${ticketClassId}`);
  }

  async updateRemainingTickets(flightId: number, ticketClassId: number, quantity: number): Promise<void> {
    return apiClient.put(`/flight-ticket-classes/${flightId}/${ticketClassId}/update-remaining?quantity=${quantity}`);
  }

  async getAvailableFlightTicketClasses(): Promise<FlightTicketClass[]> {
    return apiClient.get('/flight-ticket-classes/available');
  }
}

export const flightTicketClassService = new FlightTicketClassService();
```
#### passengerService.ts ⚠️ **MISSING**
```typescript
// filepath: d:\flight-management-system\frontend\src\services\passengerService.ts
import { apiClient } from './api';

export interface Passenger {
  passengerId?: number;
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface CreatePassengerRequest {
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface UpdatePassengerRequest {
  passengerName?: string;
  email?: string;
  phoneNumber?: string;
}

class PassengerService {
  async getAllPassengers(): Promise<Passenger[]> {
    return apiClient.get('/passengers');
  }

  async getPassengerById(id: number): Promise<Passenger> {
    return apiClient.get(`/passengers/${id}`);
  }

  async getPassengerByCitizenId(citizenId: string): Promise<Passenger> {
    return apiClient.get(`/passengers/citizen-id/${citizenId}`);
  }

  async createPassenger(data: CreatePassengerRequest): Promise<Passenger> {
    return apiClient.post('/passengers', data);
  }

  async updatePassenger(id: number, data: UpdatePassengerRequest): Promise<Passenger> {
    return apiClient.put(`/passengers/${id}`, data);
  }

  async deletePassenger(id: number): Promise<void> {
    return apiClient.delete(`/passengers/${id}`);
  }

  async searchPassengersByName(name: string): Promise<Passenger[]> {
    return apiClient.get(`/passengers/search/${name}`);
  }

  async getPassengersByEmail(email: string): Promise<Passenger[]> {
    return apiClient.get(`/passengers/email/${email}`);
  }
}

export const passengerService = new PassengerService();
```
### Service Index Updates

#### services/index.ts
```typescript
// filepath: d:\flight-management-system\frontend\src\services\index.ts
// ...existing exports...

// Add missing service exports
export { flightTicketClassService } from './flightTicketClassService';
export { passengerService } from './passengerService';

// Export types for missing services
export type { 
  FlightTicketClass, 
  CreateFlightTicketClassRequest, 
  UpdateFlightTicketClassRequest 
} from './flightTicketClassService';

export type { 
  Passenger, 
  CreatePassengerRequest, 
  UpdatePassengerRequest 
} from './passengerService';
```
### API Integration Fixes

#### flightService.ts Updates
```typescript
// filepath: d:\flight-management-system\frontend\src\services\flightService.ts
// ...existing code...

// Add flight availability checking
async checkFlightAvailability(flightId: number): Promise<FlightTicketClass[]> {
  return apiClient.get(`/flight-ticket-classes/flight/${flightId}`);
}

// Fix search method to handle backend requirements properly
async searchFlights(criteria: FlightSearchCriteria): Promise<Flight[]> {
  const params: any = {
    departureAirportId: criteria.departureAirportId,
    arrivalAirportId: criteria.arrivalAirportId,
    departureDate: criteria.departureDate,
    passengerCount: criteria.passengerCount
  };

  // Only include returnDate if provided
  if (criteria.returnDate) {
    params.returnDate = criteria.returnDate;
  }

  // Only include ticketClassId if specified (avoid 500 errors)
  if (criteria.ticketClassId && criteria.ticketClassId > 0) {
    params.ticketClassId = criteria.ticketClassId;
  }

  return apiClient.get('/flights/search', { params });
}
```

## Authentication & Authorization

### Account Type Mapping Issues

#### Current Frontend Logic (INCORRECT)
```typescript
// Frontend assumes (WRONG per database schema):
interface Account {
  accountType: number; // 1 = admin/employee, 2 = customer
}

const usePermissions = () => {
  const { user } = useAuth();
  
  return {
    canViewAdmin: user?.accountType === 1,        // WRONG: should be 2
    canManageFlights: user?.accountType === 1,    // WRONG: should be 2  
    canBookTickets: user?.accountType === 2,      // WRONG: should be 1
    canViewOwnBookings: !!user
  };
};
```

#### Required Fixes
```typescript
// Option 1: Fix frontend to match database schema
const usePermissions = () => {
  const { user } = useAuth();
  
  return {
    canViewAdmin: user?.accountType === 2,        // ✅ Employee per DB
    canManageFlights: user?.accountType === 2,    // ✅ Employee per DB
    canBookTickets: user?.accountType === 1,      // ✅ Customer per DB
    canViewOwnBookings: !!user
  };
};

// Option 2: Fix database to match frontend expectations
// UPDATE account SET account_type = CASE 
//   WHEN account_type = 1 THEN 2
//   WHEN account_type = 2 THEN 1
// END;
```
### Route Protection Updates
```typescript
// Fix protected routes based on correct account types
<Route path="/admin/*" element={
  <ProtectedRoute requireAccountType={2}> {/* Employee */}
    <AdminRoutes />
  </ProtectedRoute>
} />

<Route path="/dashboard" element={
  <ProtectedRoute requireAccountType={1}> {/* Customer */}
    <Dashboard />
  </ProtectedRoute>
} />
```

## Models & Types

### Updated Type Definitions

#### Employee.ts - **NEW ADDITION**
```typescript
// filepath: d:\flight-management-system\frontend\src\models\Employee.ts
export interface Employee {
  employeeId?: number;
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  citizenId?: string;
  employeeType: number; // 1-5: Different employee roles per database
}

export interface CreateEmployeeRequest {
  accountName: string;
  password: string;
  email: string;
  phoneNumber: string;
  citizenId: string;
  employeeType: number;
}

export interface UpdateEmployeeRequest {
  accountName?: string;
  email?: string;
  phoneNumber?: string;
  employeeType?: number;
}

// Employee types per database schema
export enum EmployeeType {
  FLIGHT_SCHEDULE_RECEPTION = 1, // tiep nhan lich bay
  TICKET_SALES_BOOKING = 2,      // ban/dat ve
  CUSTOMER_SERVICE = 3,          // cskh
  ACCOUNTING = 4,                // ke toan
  SYSTEM_ADMINISTRATOR = 5       // sa
}
```
#### FlightTicketClass.ts - **MISSING CRITICAL TYPE**
```typescript
// filepath: d:\flight-management-system\frontend\src\models\FlightTicketClass.ts
export interface FlightTicketClass {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
  
  // Extended properties from joins
  ticketClassName?: string;
  color?: string;
  flightCode?: string;
  isAvailable?: boolean;
  
  // Navigation properties
  flight?: Flight;
  ticketClass?: TicketClass;
}

export interface FlightTicketClassId {
  flightId: number;
  ticketClassId: number;
}

export interface CreateFlightTicketClassRequest {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
}

export interface UpdateFlightTicketClassRequest {
  ticketQuantity?: number;
  remainingTicketQuantity?: number;
  specifiedFare?: number;
}
```
#### Passenger.ts - **MISSING TYPE**
```typescript
// filepath: d:\flight-management-system\frontend\src\models\Passenger.ts
export interface Passenger {
  passengerId?: number;
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface CreatePassengerRequest {
  passengerName: string;
  email: string;
  citizenId: string;
  phoneNumber: string;
}

export interface UpdatePassengerRequest {
  passengerName?: string;
  email?: string;
  phoneNumber?: string;
}

```
#### Parameter.ts - **ENHANCED WITH DB FIELDS**
```typescript
// filepath: d:\flight-management-system\frontend\src\models\Parameter.ts
export interface Parameter {
  id?: number;
  maxMediumAirport: number;              // Max layover airports
  minFlightDuration: number;             // Minutes
  minLayoverDuration: number;            // Minutes
  maxLayoverDuration: number;            // Minutes
  minBookingInAdvanceDuration: number;   // Minutes - how far in advance booking required
  maxBookingHoldDuration: number;        // Minutes - how long unpaid tickets held
  deletedAt?: string;
}

export interface CreateParameterRequest {
  maxMediumAirport: number;
  minFlightDuration: number;
  minLayoverDuration: number;
  maxLayoverDuration: number;
  minBookingInAdvanceDuration: number;
  maxBookingHoldDuration: number;
}

export interface UpdateParameterRequest {
  maxMediumAirport?: number;
  minFlightDuration?: number;
  minLayoverDuration?: number;
  maxLayoverDuration?: number;
  minBookingInAdvanceDuration?: number;
  maxBookingHoldDuration?: number;
}
```
#### Ticket.ts - **UPDATED WITH DB FIELDS**
```typescript
// filepath: d:\flight-management-system\frontend\src\models\Ticket.ts
export interface Ticket {
  ticketId: number;                    // Manual ID assignment required per DB
  flightId: number;
  ticketClassId: number;
  bookCustomerId?: number;             // Nullable per DB schema
  passengerId: number;
  seatNumber: string;                  // Max 7 characters per DB
  ticketStatus: number;                // 1: paid, 2: unpaid, 3: canceled
  paymentTime?: string;                // Nullable per DB schema
  fare: number;
  deletedAt?: string;

  // Navigation properties (populated by backend joins)
  flight?: Flight;
  ticketClass?: TicketClass;
  bookCustomer?: Customer;
  passenger?: Passenger;
}

export enum TicketStatus {
  PAID = 1,
  UNPAID = 2,
  CANCELED = 3
}
```
### Completion Status
- **Authentication Flow**: 70% (works but wrong role mapping)
- **Flight Search**: 60% (basic search works, advanced features broken)
- **Booking Flow**: 40% (structure ready but data transformation issues)
- **Admin Panel**: 50% (basic CRUD works, missing entity management)
- **Service Layer**: 60% (core services work, critical ones missing)

### Next Steps Priority
1. **Fix account type mapping** (critical authentication issue)
2. **Implement missing services** (FlightTicketClass, Passenger)
3. **Fix booking data transformation** (passenger name handling)
4. **Test end-to-end workflows** (search → book → pay)
5. **Implement missing admin components** (TicketClass, Plane management)

This frontend documentation now accurately reflects the current implementation state, identifies critical issues, and provides clear guidance for completing the missing functionality to achieve full system integration.
