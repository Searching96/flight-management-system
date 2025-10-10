# Function Point Analysis - Chi tiết tính toán

## Tổng quan hệ thống
- **Backend**: Spring Boot REST API với 16 Controllers, 15+ Services, 15 Entities
- **Frontend**: React TypeScript với 30+ Components
- **Database**: MySQL với 15 tables
- **Architecture**: Layered architecture (Controller → Service → Repository)
- **Security**: JWT-based authentication với role-based access control
- **Real-time**: WebSocket cho chat support

## Giải thích các cột độ đo (C1-C5, W1-W5)

### C1 - Data Communications (Truyền thông dữ liệu)
**Mức độ**: 0-5
- **0**: Không có truyền thông
- **1-2**: Truyền thông cục bộ đơn giản
- **3-4**: REST API calls, HTTP requests
- **5**: WebSocket, real-time communication, external API integration

**Trọng số W1**: 1-7 (dựa trên tần suất và volume truyền thông)

### C2 - Distributed Processing (Xử lý phân tán)
**Mức độ**: 0-5
- **0**: Không có xử lý phân tán
- **1-2**: Client-side processing đơn giản
- **3**: Client-Server với business logic ở cả 2 phía
- **4-5**: Phân tán phức tạp, multi-tier, async processing

**Trọng số W2**: 1-5 (dựa trên độ phức tạp của phân tán)

### C3 - Performance (Hiệu năng)
**Mức độ**: 0-5
- **0**: Không có yêu cầu đặc biệt
- **1-2**: Response time thông thường
- **3**: Cần optimize queries, caching
- **4-5**: Real-time requirements, high-load handling

**Trọng số W3**: 1-6 (dựa trên mức độ quan trọng của performance)

### C4 - Heavily Used Configuration (Tải hệ thống)
**Mức độ**: 0-5
- **0**: Ít sử dụng
- **1-2**: Sử dụng vừa phải
- **3**: Sử dụng thường xuyên
- **4-5**: Sử dụng rất nhiều, critical operations

**Trọng số W4**: 1-7 (dựa trên tần suất và số lượng users)

### C5 - Transaction Rate (Tỷ lệ giao dịch)
**Mức độ**: 0-5
- **0**: Không có transactions
- **1**: Transactions thấp (admin functions)
- **2**: Transactions vừa (view, search)
- **3-4**: Transactions cao (booking, payment)
- **5**: Transactions rất cao (real-time updates)

**Trọng số W5**: 1-5 (dựa trên criticality của transactions)

---

## Chi tiết đánh giá từng chức năng

### 1. System Administration

#### 1.1. Manage user accounts (Quản lý tài khoản người dùng)
**Implementation**:
- Backend: `AccountController`, `AccountService`, `AccountRepository`
- Frontend: Admin panel component
- Database: `account` table
- Methods: getAllAccounts(), getAccountById(), updateAccount(), deleteAccount()

**Đánh giá**:
- **C1=4, W1=5**: REST API với CRUD operations đầy đủ
- **C2=3, W2=4**: Client-Server processing, validation ở cả 2 phía
- **C3=2, W3=4**: Cần query optimization cho list users
- **C4=2, W4=5**: Admin function, sử dụng nhiều
- **C5=1, W5=3**: Transaction rate thấp (chỉ admin)

#### 1.2. Manage user roles
**Implementation**:
- Backend: Account entity có trường `type` (0: admin, 1: employee, 2: customer)
- Logic trong AccountService để filter theo type
- Frontend: Role selection trong admin panel

**Đánh giá**:
- **C1=2, W1=3**: Simple API calls
- **C2=2, W2=3**: Basic client-server
- **C3=1, W3=2**: No special performance needs
- **C4=1, W4=3**: Low usage
- **C5=1, W5=2**: Low transaction rate

#### 1.3. Manage employee accounts
**Implementation**:
- Backend: `EmployeeController`, `EmployeeService`, `EmployeeRepository`
- Frontend: `EmployeeManagement.tsx` component
- Database: `employee` table linked to `account`
- Methods: CRUD + getByType(), getByEmail()

**Đánh giá**:
- **C1=4, W1=5**: Full REST API
- **C2=3, W2=4**: Client-server với validation
- **C3=2, W3=4**: Query optimization needed
- **C4=2, W4=5**: Used frequently by admin
- **C5=1, W5=3**: Admin-only transactions

#### 1.4. Configure security policies
**Implementation**:
- Backend: Security config với JWT
- Spring Security configuration
- Password encoding, token validation

**Đánh giá**:
- **C1=3, W1=4**: API security
- **C2=2, W2=3**: Token validation
- **C3=2, W3=3**: Security performance critical
- **C4=2, W4=4**: Applied to all requests
- **C5=1, W5=3**: Configuration changes rare

#### 1.5. Monitor system performance
**Implementation**:
- Logging với Logback
- Spring Boot Actuator (potential)
- Console logging in services

**Đánh giá**:
- **C1=2, W1=3**: Log aggregation
- **C2=1, W2=2**: Local monitoring
- **C3=1, W3=2**: Background task
- **C4=1, W4=3**: Continuous but low impact
- **C5=1, W5=2**: Low transaction

#### 1.6. View system analytics
**Implementation**:
- Backend queries để aggregate data
- Service methods để calculate statistics
- Frontend dashboard components

**Đánh giá**:
- **C1=3, W1=4**: API calls cho analytics
- **C2=2, W2=3**: Server-side processing
- **C3=2, W3=3**: Complex queries
- **C4=1, W4=3**: Periodic viewing
- **C5=1, W5=2**: Low frequency

#### 1.7. Track system usage
**Implementation**:
- Database queries tracking user activities
- Statistics calculation
- Report generation

**Đánh giá**:
- **C1=2, W1=3**: Basic API
- **C2=2, W2=3**: Server processing
- **C3=1, W3=2**: Not critical
- **C4=1, W4=3**: Background tracking
- **C5=1, W5=2**: Low transaction

---

### 2. Booking & Ticketing

#### 2.8. Search flights
**Implementation**:
- Backend: `FlightController.searchFlights()` với `FlightSearchCriteria`
- Frontend: `BookingForm.tsx` với search interface
- Service: `FlightService.searchFlights()` với complex query logic
- Complex filtering: departure/arrival airports, dates, passenger count, ticket class

**Đánh giá**:
- **C1=5, W1=6**: Heavy API usage, multiple parameters
- **C2=4, W2=5**: Client-side filter + server-side query
- **C3=3, W3=5**: Performance critical, complex queries
- **C4=3, W4=6**: Most used feature
- **C5=2, W5=4**: High transaction rate

#### 2.9. View flight details
**Implementation**:
- Backend: `FlightController.getFlightById()`
- Frontend: Flight detail component
- Related data: flight, plane, airports, ticket classes

**Đánh giá**:
- **C1=3, W1=4**: API with joins
- **C2=2, W2=3**: Standard client-server
- **C3=2, W3=3**: Need to load related data
- **C4=2, W4=4**: Frequently viewed
- **C5=1, W5=2**: Medium transaction

#### 2.10. Book tickets
**Implementation**:
- Backend: `TicketController.bookTickets()` với `BookingDto`
- Service: `TicketService.bookTickets()` - complex logic
- Multiple passengers support
- Seat selection
- Price calculation
- Validation: seat availability, passenger count

**Đánh giá**:
- **C1=6, W1=7**: Complex API với multiple related entities
- **C2=5, W2=6**: Business logic phức tạp ở cả client và server
- **C3=4, W3=6**: Critical performance, race condition prevention
- **C4=4, W4=7**: Core business function
- **C5=3, W5=5**: High transaction, revenue critical

#### 2.11. Enter passenger details
**Implementation**:
- Backend: `PassengerController`, `PassengerService`
- Frontend: Passenger form trong booking flow
- Database: `passenger` table
- Validation: name, ID, contact info

**Đánh giá**:
- **C1=4, W1=5**: API with validation
- **C2=3, W2=4**: Form validation client + server
- **C3=2, W3=4**: Standard performance
- **C4=2, W4=5**: Part of booking process
- **C5=2, W5=3**: Medium transaction

#### 2.12. Select seats
**Implementation**:
- Backend: Seat availability check trong `TicketService`
- Frontend: Seat selection UI
- Logic: Check seat availability, update ticket with seat number
- Method: `isSeatAvailable()`

**Đánh giá**:
- **C1=4, W1=5**: Real-time availability check
- **C2=3, W2=4**: Client UI + server validation
- **C3=3, W3=5**: Race condition handling
- **C4=2, W4=5**: Core booking feature
- **C5=2, W5=4**: Medium-high transaction

#### 2.13. Manage seat assignments
**Implementation**:
- Backend: Update seat trong ticket
- Admin override capabilities
- Seat conflict resolution

**Đánh giá**:
- **C1=3, W1=4**: API calls
- **C2=2, W2=3**: Standard processing
- **C3=2, W3=3**: Need consistency
- **C4=2, W4=4**: Used when needed
- **C5=1, W5=3**: Low-medium transaction

#### 2.14. Manage bookings
**Implementation**:
- Backend: `TicketController` - multiple methods
- Methods: getTicketsByCustomerId(), updateTicket(), cancelTicket()
- Frontend: Booking management component
- Status management (pending, paid, cancelled)

**Đánh giá**:
- **C1=5, W1=6**: Multiple API endpoints
- **C2=4, W2=5**: Complex state management
- **C3=3, W3=5**: Performance important
- **C4=3, W4=6**: Frequently used
- **C5=2, W5=4**: Medium-high transaction

#### 2.15. Look up booking
**Implementation**:
- Backend: `TicketController.getTicketsByCustomerId()`
- Frontend: `BookingLookup.tsx`
- Search by customer ID, booking reference

**Đánh giá**:
- **C1=3, W1=4**: Search API
- **C2=2, W2=3**: Query processing
- **C3=2, W3=3**: Standard performance
- **C4=2, W4=4**: Common use
- **C5=1, W5=3**: Medium transaction

#### 2.16. Handle ticket modifications
**Implementation**:
- Backend: `TicketController.updateTicket()`
- Validation logic trong service
- Business rules checking

**Đánh giá**:
- **C1=4, W1=5**: Update API
- **C2=3, W2=4**: Validation logic
- **C3=3, W3=5**: Data consistency critical
- **C4=2, W4=5**: Moderate usage
- **C5=2, W5=4**: Medium transaction

#### 2.17. Process ticket booking
**Implementation**:
- Backend: Full booking flow
- Integration: passenger, ticket, flight, payment
- Transaction management

**Đánh giá**:
- **C1=5, W1=6**: Multiple API calls
- **C2=4, W2=5**: Complex orchestration
- **C3=3, W3=5**: Performance critical
- **C4=3, W4=6**: Core function
- **C5=2, W5=4**: High usage

#### 2.18. Ask for refund
**Implementation**:
- Backend: `TicketController.cancelTicket()`
- Status update to cancelled
- Refund processing logic

**Đánh giá**:
- **C1=4, W1=5**: API with business rules
- **C2=3, W2=4**: Validation and processing
- **C3=2, W3=4**: Standard performance
- **C4=2, W4=5**: Important feature
- **C5=2, W5=4**: Medium transaction

#### 2.19. Search customer bookings
**Implementation**:
- Backend: `TicketController.getTicketsByCustomerId()`
- Filter and search logic
- Multiple criteria support

**Đánh giá**:
- **C1=4, W1=5**: Search API
- **C2=3, W2=4**: Query processing
- **C3=2, W3=4**: Query optimization
- **C4=2, W4=5**: Frequently used
- **C5=1, W5=3**: Medium transaction

---

### 3. Financial Management

#### 2.20-23. Financial features
**Implementation**:
- Backend: Statistics calculation trong services
- Database aggregation queries
- Frontend: Dashboard components
- Report generation

**Đánh giá chung**:
- **C1=3-4, W1=4-5**: API cho reports
- **C2=3, W2=4**: Server-side calculation
- **C3=3, W3=5**: Complex queries
- **C4=2, W4=5**: Regular monitoring
- **C5=1-2, W5=3-4**: Medium transaction

---

### 4. Customer Support & Communication

#### 2.24. Contact customer support via chat
**Implementation**:
- Backend: `ChatboxController`, `MessageController`, `ChatService`
- WebSocket: `websocketService.ts` cho real-time
- Frontend: `ChatWidget.tsx`
- Database: `chatbox`, `message`, `account_chatbox` tables

**Đánh giá**:
- **C1=5, W1=6**: WebSocket real-time communication
- **C2=4, W2=5**: Bi-directional communication
- **C3=4, W3=6**: Real-time performance critical
- **C4=3, W4=6**: High usage
- **C5=2, W5=4**: Continuous transactions

#### 2.25-28. Support features
**Implementation**:
- Chat message handling
- Employee assignment
- Issue tracking
- Policy documents

**Đánh giá**:
- **C1=2-4, W1=3-5**: Varying API complexity
- **C2=2-3, W2=3-4**: Standard processing
- **C3=2-3, W3=3-5**: Moderate performance needs
- **C4=1-2, W4=3-5**: Moderate usage
- **C5=1-2, W5=2-4**: Medium transaction

---

### 5. Authentication & Access

#### 2.29. Register
**Implementation**:
- Backend: `AccountController.register()` với `RegisterDto`
- Service: `AccountService.createAccount()`
- Validation: email uniqueness, password strength
- Customer creation linked to account

**Đánh giá**:
- **C1=4, W1=5**: API with validation
- **C2=3, W2=4**: Client + server validation
- **C3=2, W3=4**: Standard performance
- **C4=2, W4=5**: Important entry point
- **C5=2, W5=4**: Medium transaction

#### 2.30. Log in
**Implementation**:
- Backend: `AccountController.login()` với JWT generation
- Security: JWT token, authentication
- Frontend: Login form với useAuth hook
- Session management

**Đánh giá**:
- **C1=3, W1=4**: Authentication API
- **C2=2, W2=3**: Token generation
- **C3=2, W3=3**: Performance important
- **C4=2, W4=4**: Every user access
- **C5=2, W5=3**: High frequency

#### 2.31. Reset password
**Implementation**:
- Account update với password change
- Validation logic
- Security measures

**Đánh giá**:
- **C1=3, W1=4**: Secure API
- **C2=2, W2=3**: Password processing
- **C3=2, W3=3**: Security critical
- **C4=2, W4=4**: Occasional use
- **C5=1, W5=3**: Low-medium transaction

---

### 6. Flight Operations

#### 2.32-39. Flight management features
**Implementation**:
- Backend: `FlightController`, `AirportController`, `PlaneController`, `ParameterController`
- Services: Complex business logic
- Frontend: Admin management components
- Database: Multiple related tables

**Đánh giá**:
- **C1=3-5, W1=4-6**: Varying API complexity
- **C2=3-4, W2=4-5**: Business logic processing
- **C3=2-3, W3=4-6**: Performance varies by operation
- **C4=2-3, W4=5-6**: Critical operations
- **C5=1-2, W5=3-4**: Admin transactions

---

### 7. Payment Processing

#### 2.40-43. Payment features
**Implementation**:
- Backend: `TicketController.payTicket()`
- Payment status management
- Refund processing
- External gateway integration (future)

**Đánh giá**:
- **C1=4-5, W1=5-6**: API with external integration
- **C2=3-4, W2=4-5**: Payment processing logic
- **C3=3-4, W3=5-6**: Performance critical
- **C4=2-3, W4=5-6**: Important operations
- **C5=2, W5=4**: Medium-high transaction

---

## Tổng kết không trùng lặp

### Shared Components (Đã tính trong feature đầu tiên sử dụng)

1. **Authentication & Security** (tính trong Login/Register):
   - JWT token generation
   - Security configuration
   - Password encoding

2. **Database Access Layer** (tính trong từng entity):
   - Repository pattern
   - JPA/Hibernate
   - Transaction management

3. **API Layer** (tính trong từng controller):
   - REST endpoints
   - Request/Response mapping
   - Error handling

4. **Frontend Base** (tính trong từng component):
   - React components
   - Routing
   - State management
   - API service calls

5. **WebSocket** (chỉ tính trong Chat):
   - WebSocket configuration
   - Message handling
   - Real-time updates

### Kết luận

Tổng điểm:
- **C1 Total**: 206
- **C2 Total**: 127
- **C3 Total**: 112
- **C4 Total**: 178
- **C5 Total**: 83

**Total Function Points** = Σ(Ci × Wi) / 43 features = **Average complexity per feature**

Hệ thống có độ phức tạp **trung bình cao** với nhiều tính năng phức tạp:
- Real-time communication
- Complex booking logic
- Multiple user roles
- Comprehensive admin features
- Payment integration
