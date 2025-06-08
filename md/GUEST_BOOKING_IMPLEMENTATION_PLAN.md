# Flight Management System - Guest Booking Implementation Plan

## Executive Summary

The Flight Management System already has substantial guest booking capabilities in place, with the backend supporting `customerId: null` for walk-in bookings and the frontend having a frequent flyer checkbox that controls customer ID assignment. However, there are critical issues and missing functionality that need to be addressed.

## Current State Analysis

### ✅ What's Already Working

1. **Backend Guest Booking Support**
   - `BookingDto` supports `customerId: null` for guest bookings
   - `TicketServiceImpl.bookTickets()` validates and processes null customer IDs
   - Database schema allows `book_customer_id` to be null in tickets table
   - Booking validation handles guest bookings correctly

2. **Frontend Basic Guest Support**
   - `BookingForm.tsx` has frequent flyer checkbox that sets `customerId` to null when unchecked
   - Authentication is correctly fixed - `usePermissions` now matches database schema
   - Data transformation for passenger names (firstName + lastName → passengerName) is implemented

3. **Testing Infrastructure**
   - Comprehensive testing framework with Vitest (frontend) and Spring Boot Test (backend)
   - Existing integration tests for booking scenarios
   - Good test coverage patterns established

### ❌ Critical Issues Identified

1. **Account Type Mapping** - ✅ ALREADY FIXED
   - The `usePermissions` hook has been corrected to match database schema
   - accountType 1 = Customer, accountType 2 = Employee

2. **Missing Service Implementations**
   - `PassengerService.ts` - Frontend passenger management service missing
   - Incomplete guest booking UI flow (no guest-specific forms)

3. **Data Structure Issues**
   - Frontend uses firstName/lastName, backend expects passengerName (ALREADY HANDLED in BookingForm.tsx)

4. **Guest Booking Management**
   - No way for guests to retrieve bookings later
   - No confirmation codes or email notifications
   - No guest booking history/management

## Implementation Plan

### Phase 1: Fix Missing Services (HIGH PRIORITY)

#### 1.1 Create Missing Frontend Services
- [x] Account type mapping fixed in usePermissions
- [ ] Create complete PassengerService.ts
- [ ] Enhance guest booking flow in BookingForm.tsx
- [ ] Add guest booking confirmation system

#### 1.2 Complete Backend Guest Booking Features
- [ ] Add booking confirmation code generation
- [ ] Create guest booking retrieval endpoints
- [ ] Add email notification service (optional)

### Phase 2: Enhanced Guest Booking UI (MEDIUM PRIORITY)

#### 2.1 Guest-Specific UI Components
- [ ] Guest booking form without login requirement
- [ ] Booking confirmation page with booking code
- [ ] Guest booking lookup/management page
- [ ] Enhanced validation for guest bookings

#### 2.2 Booking Management Features
- [ ] Booking retrieval by confirmation code
- [ ] Guest booking cancellation
- [ ] Booking modification (limited scope)

### Phase 3: Comprehensive Unit Testing (HIGH PRIORITY)

#### 3.1 Backend Testing
- [ ] Guest booking service tests
- [ ] Booking confirmation tests
- [ ] Edge case testing (null customer scenarios)
- [ ] Integration tests for guest flows

#### 3.2 Frontend Testing
- [ ] Guest booking form tests
- [ ] Permission system tests
- [ ] Service layer tests
- [ ] Component integration tests

### Phase 4: System Integration & Polish (LOW PRIORITY)

#### 4.1 End-to-End Guest Experience
- [ ] Complete guest booking flow testing
- [ ] Performance optimization
- [ ] Error handling improvements
- [ ] Documentation updates

## Detailed Implementation Tasks

### Task 1: Create PassengerService.ts
**Priority**: HIGH
**Estimated Time**: 2 hours

Create a complete frontend service for passenger management to support the existing backend passenger endpoints.

### Task 2: Enhance Guest Booking Flow
**Priority**: HIGH  
**Estimated Time**: 4 hours

Improve the existing guest booking functionality in BookingForm.tsx to provide a better guest experience.

### Task 3: Add Booking Confirmation System
**Priority**: MEDIUM
**Estimated Time**: 3 hours

Implement booking confirmation codes and retrieval system for guest bookings.

### Task 4: Create Comprehensive Unit Tests
**Priority**: HIGH
**Estimated Time**: 6 hours

Add comprehensive unit tests for both frontend and backend guest booking functionality.

### Task 5: Guest Booking Management UI
**Priority**: MEDIUM
**Estimated Time**: 4 hours

Create UI components for guests to manage their bookings without requiring account registration.

## Success Metrics

1. **Functional Requirements**
   - [x] Guests can book flights without creating accounts
   - [ ] Guests can retrieve bookings using confirmation codes
   - [ ] All booking data validation works correctly
   - [ ] System handles guest vs. registered user bookings seamlessly

2. **Technical Requirements**
   - [x] Account type mapping consistency between frontend and backend
   - [ ] Complete unit test coverage (>90%) for guest booking flows
   - [ ] No data transformation errors between frontend and backend
   - [ ] All services properly implemented and tested

3. **User Experience Requirements**
   - [ ] Intuitive guest booking flow
   - [ ] Clear booking confirmation and management
   - [ ] Proper error handling and user feedback
   - [ ] Responsive design for all guest booking components

## Risk Assessment

### Low Risk ✅
- Account type mapping (already fixed)
- Basic guest booking functionality (already working)
- Testing framework setup (already established)

### Medium Risk ⚠️
- Guest booking management features (new functionality)
- Email notification integration (external dependency)
- Complex validation scenarios

### High Risk ❌
- None identified - foundation is solid

## Timeline

- **Week 1**: Complete missing services and enhance guest booking flow
- **Week 2**: Implement booking confirmation system and guest management UI  
- **Week 3**: Create comprehensive unit tests
- **Week 4**: Integration testing and polish

## Next Steps

1. Start with creating PassengerService.ts (Task 1)
2. Enhance the existing guest booking flow (Task 2)
3. Implement unit tests as we go (Task 4)
4. Add booking confirmation system (Task 3)
5. Create guest management UI (Task 5)

This plan builds on the solid foundation already in place and focuses on completing the missing pieces to provide a comprehensive guest booking experience.
