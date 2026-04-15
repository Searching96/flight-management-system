# Tài Liệu Design Patterns

## Hệ Thống Quản Lý Chuyến Bay (Flight Management System)

Tài liệu này mô tả các mẫu thiết kế (Design Patterns) được áp dụng trong Hệ thống Quản lý Chuyến bay, bao gồm các đoạn code cụ thể thực hiện mỗi mẫu thiết kế và mục đích sử dụng của chúng trong dự án.

---

## 📚 Giới Thiệu Về Design Patterns

**Design Pattern (Mẫu thiết kế)** là các giải pháp tái sử dụng được đúc kết để giải quyết các vấn đề phổ biến trong thiết kế phần mềm.

Hệ thống này sử dụng phân loại theo **Gang of Four (GoF)**, chia thành 3 nhóm chính.

Quan tâm đến giao tiếp và phân công trách nhiệm giữa các đối tượng.

---

# PHẦN I: NHÓM KHỞI TẠO (CREATIONAL PATTERNS)

Nhóm các mẫu thiết kế giải quyết vấn đề khởi tạo đối tượng, giúp hệ thống linh hoạt và độc lập với cách thức tạo ra các đối tượng.

---

## 1. Singleton Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Singleton** - Đảm bảo một class chỉ có duy nhất một instance và cung cấp một điểm truy cập toàn cục đến nó.

### 💻 Triển Khai Trong Code

**Singleton thông qua Spring Bean**

```java
// File: backend/src/main/java/com/flightmanagement/service/impl/FlightServiceImpl.java
@Service  // Spring quản lý bean này như một Singleton
@Transactional
public class FlightServiceImpl implements FlightService {
    // Spring chỉ tạo duy nhất một instance của service này trong toàn ứng dụng

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public FlightServiceImpl(FlightRepository flightRepository,
                             FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
    }
}
```

**Singleton cho WebSocket Handler**

```java
// File: backend/src/main/java/com/flightmanagement/websocket/ChatWebSocketHandler.java
@Component  // Spring singleton
public class ChatWebSocketHandler implements WebSocketHandler {
    // Trạng thái được chia sẻ cho tất cả các kết nối WebSocket
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> typingUsers = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToChatbox = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);  // Lưu vào state chung
        System.out.println("WebSocket connection established: " + sessionId);
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Tiết kiệm tài nguyên**: Chỉ có một instance của mỗi service tồn tại, giảm thiểu việc tạo đối tượng
- **Trạng thái chia sẻ**: WebSocket handler duy trì trạng thái chung cho tất cả các kết nối
- **Mặc định của Spring**: Tất cả các bean `@Service`, `@Component`, `@Repository` đều là Singleton
- **Thread-safe**: Phải sử dụng các collection an toàn luồng (ConcurrentHashMap) cho Singleton

---

## 2. Builder Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Builder** - Tách biệt việc xây dựng một đối tượng phức tạp khỏi biểu diễn của nó, cho phép cùng một quy trình xây dựng có thể tạo ra các biểu diễn khác nhau.

### 💻 Triển Khai Trong Code

**Xây dựng đối tượng phức tạp từng bước**

```java
// File: backend/src/main/java/com/flightmanagement/config/DataInitializer.java
@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Xây dựng FlightRequest theo từng bước
        FlightRequest flightRequest = new FlightRequest();
        flightRequest.setFlightCode("VN101");
        flightRequest.setPlaneId(1);
        flightRequest.setDepartureAirportId(1);
        flightRequest.setArrivalAirportId(2);
        flightRequest.setDepartureTime(LocalDateTime.now().plusDays(1));
        flightRequest.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));

        // Xây dựng FlightDetail với các trường tùy chọn
        FlightDetailDto detail = new FlightDetailDto();
        detail.setFlightId(1);
        detail.setMediumAirportId(3);
        detail.setArrivalTime(tomorrow.withHour(11).withMinute(30));
        detail.setLayoverDuration(20);

        flightService.createFlight(flightRequest);
    }
}
```

**Builder cho API Response**

```java
// File: backend/src/main/java/com/flightmanagement/entity/ApiResponse.java
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;

    // Constructor kiểu Builder
    public ApiResponse(HttpStatus status, String message, T data, String errorCode) {
        this.status = status.is2xxSuccessful() ? "success" : "error";
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Xây dựng đối tượng phức tạp**: Tạo các đối tượng có nhiều trường tùy chọn theo từng bước
- **Dễ đọc**: Rõ ràng mỗi trường đại diện cho gì trong quá trình khởi tạo
- **Validation**: Có thể kiểm tra trạng thái đối tượng trước khi hoàn tất
- **Tính bất biến**: Có thể xây dựng các đối tượng immutable bằng constructor với tất cả các trường

---

## 3. Dependency Injection Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Dependency Injection (DI)** - Tiêm các phụ thuộc vào thông qua constructor thay vì tạo chúng bên trong class, một dạng của Inversion of Control (IoC).

### 💻 Triển Khai Trong Code

**Constructor Injection**

```java
// File: backend/src/main/java/com/flightmanagement/controller/FlightController.java
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService flightService;

    // Tiêm phụ thuộc qua Constructor
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
}
```

**Multiple Dependencies Injection**

```java
// File: backend/src/main/java/com/flightmanagement/service/impl/FlightServiceImpl.java
@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final ParameterService parameterService;
    private final FlightTicketClassService flightTicketClassService;

    // Tiêm nhiều phụ thuộc cùng lúc
    public FlightServiceImpl(FlightRepository flightRepository,
                             FlightMapper flightMapper,
                             ParameterService parameterService,
                             FlightTicketClassService flightTicketClassService) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.parameterService = parameterService;
        this.flightTicketClassService = flightTicketClassService;
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Khả năng kiểm thử**: Dễ dàng tiêm mock dependencies cho unit test
- **Loose coupling**: Các class phụ thuộc vào interface, không phải implementation cụ thể
- **Quản lý bởi Spring**: Spring tự động resolve và inject các dependencies
- **Tính bất biến**: Các trường final đảm bảo dependencies không thể thay đổi sau khi khởi tạo
- **Rõ ràng**: Tất cả dependencies hiển thị trong constructor, dễ nhìn thấy yêu cầu của class

---

# PHẦN II: NHÓM CẤU TRÚC (STRUCTURAL PATTERNS)

Nhóm các mẫu thiết kế tập trung vào cách tổ chức và kết hợp các class/đối tượng thành cấu trúc lớn hơn.

---

## 4. Facade Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Facade** - Cung cấp một giao diện đơn giản hóa cho một tập hợp các giao diện phức tạp trong một hệ thống con.

### 💻 Triển Khai Trong Code

**Service Layer như một Facade**

```java
// File: backend/src/main/java/com/flightmanagement/controller/FlightController.java
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;  // Facade đến các thao tác flight phức tạp

    @GetMapping
    public ResponseEntity<?> getAllFlights(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        // Giao diện đơn giản ẩn đi độ phức tạp của:
        // - Truy vấn database
        // - Chuyển đổi Entity sang DTO
        // - Logic phân trang
        // - Format response
        Page<FlightDto> page = flightService.getAllFlightsPaged(pageable);

        ApiResponse<?> apiResponse = new ApiResponse<>(
            HttpStatus.OK,
            "Fetched all flights",
            page,
            null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
```

**Service tổng hợp nhiều subsystem**

```java
// File: backend/src/main/java/com/flightmanagement/service/impl/FlightServiceImpl.java
@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final ParameterService parameterService;
    private final FlightTicketClassService flightTicketClassService;

    @Override
    public List<FlightDto> getAllFlights() {
        // Facade: Ẩn độ phức tạp của repository + mapping
        List<Flight> flights = flightRepository.findAllActive();
        return flightMapper.toDtoList(flights);
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Giao diện đơn giản**: Controller gọi các method service đơn giản thay vì các thao tác phức tạp
- **Phối hợp subsystem**: Service phối hợp giữa repository, mapper, validator
- **Giảm coupling**: Controller không cần biết về repository hay mapper
- **Dễ sử dụng**: Developer mới có thể sử dụng service mà không cần hiểu chi tiết bên trong

---

## 5. Mapper Pattern (Object Conversion)

### 🎯 Mẫu Thiết Kế Áp Dụng

**Mapper** - Chuyển đổi có hệ thống giữa entities và DTOs.

### 💻 Triển Khai Trong Code

**Base Mapper Interface**

```java
// File: backend/src/main/java/com/flightmanagement/mapper/BaseMapper.java
public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDtoList(List<E> entityList);
    List<E> toEntityList(List<D> dtoList);
}
```

**Concrete Mapper Implementation**

```java
// File: backend/src/main/java/com/flightmanagement/mapper/FlightMapper.java
@Component
public class FlightMapper implements BaseMapper<Flight, FlightDto> {

    private final PlaneRepository planeRepository;
    private final AirportRepository airportRepository;

    public FlightMapper(PlaneRepository planeRepository, AirportRepository airportRepository) {
        this.planeRepository = planeRepository;
        this.airportRepository = airportRepository;
    }

    @Override
    public FlightDto toDto(Flight entity) {
        if (entity == null) return null;

        FlightDto dto = new FlightDto();
        dto.setFlightId(entity.getFlightId());
        dto.setFlightCode(entity.getFlightCode());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setArrivalTime(entity.getArrivalTime());

        if (entity.getPlane() != null) {
            dto.setPlaneId(entity.getPlane().getPlaneId());
            dto.setPlaneCode(entity.getPlane().getPlaneCode());
        }

        if (entity.getDepartureAirport() != null) {
            dto.setDepartureAirportId(entity.getDepartureAirport().getAirportId());
            dto.setDepartureAirportName(entity.getDepartureAirport().getAirportName());
            dto.setDepartureCityName(entity.getDepartureAirport().getCityName());
        }

        return dto;
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Logic chuyển đổi tập trung**: Tất cả logic chuyển đổi entity-to-DTO ở một nơi
- **Tái sử dụng**: Cùng một mapper được dùng trong tất cả service cần chuyển đổi Flight
- **Nhất quán**: Đảm bảo tất cả conversions theo cùng một pattern
- **Xử lý null an toàn**: Xử lý entities và relationships null một cách graceful
- **Flatten relationships**: Chuyển đổi entity graph phức tạp thành DTO phẳng cho API response

---

# PHẦN III: NHÓM HÀNH VI (BEHAVIORAL PATTERNS)

Nhóm các mẫu thiết kế quan tâm đến giao tiếp và phân công trách nhiệm giữa các đối tượng.

---

## 6. Strategy Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Strategy** - Định nghĩa một họ các thuật toán, đóng gói từng thuật toán lại và làm cho chúng có thể thay thế lẫn nhau.

### 💻 Triển Khai Trong Code

**Strategy Configuration**

```java
// File: backend/src/main/java/com/flightmanagement/security/SecurityConfig.java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Strategy: Thuật toán BCrypt
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());  // Inject strategy
        return authProvider;
    }
}
```

**Sử dụng Strategy trong Service**

```java
// File: backend/src/main/java/com/flightmanagement/service/impl/AuthServiceImpl.java
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;  // Strategy interface

    @Override
    public AuthResponse login(LoginRequestDto request) {
        // Strategy pattern đang hoạt động - thuật toán được đóng gói
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        // ... tạo token
    }

    @Override
    public void register(RegisterDto request) {
        // Sử dụng password encoding strategy
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // ... lưu account
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Đóng gói thuật toán**: Thuật toán hash password có thể đổi mà không sửa service code
- **Lựa chọn runtime**: Các authentication strategy khác nhau có thể được dùng dựa trên config
- **Open/Closed Principle**: Dễ dàng thêm password encoder mới mà không thay đổi code hiện có
- **Khả năng kiểm thử**: Mock password encoder có thể được inject cho testing

---

## 7. Observer Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Observer** - Định nghĩa mối quan hệ phụ thuộc một-nhiều, khi một đối tượng thay đổi trạng thái, tất cả các đối tượng phụ thuộc sẽ được thông báo và cập nhật tự động.

### 💻 Triển Khai Trong Code

```java
// File: backend/src/main/java/com/flightmanagement/websocket/ChatWebSocketHandler.java
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    // Subject duy trì danh sách observers (WebSocket sessions)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToChatbox = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);  // Đăng ký observer
        System.out.println("WebSocket connection established: " + sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        String sessionId = session.getId();
        try {
            JsonNode data = objectMapper.readTree(message.getPayload().toString());
            String type = data.get("type").asText();

            switch (type) {
                case "new_message":
                    handleNewMessage(sessionId, data);  // Thông báo tất cả observers
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling WebSocket message: " + e.getMessage());
        }
    }

    private void handleNewMessage(String sessionId, JsonNode data) {
        String chatboxId = data.get("chatboxId").asText();

        // Thông báo tất cả observers (sessions) trong cùng chatbox
        sessions.forEach((sid, session) -> {
            if (sessionToChatbox.get(sid) != null &&
                sessionToChatbox.get(sid).equals(chatboxId)) {
                try {
                    session.sendMessage(new TextMessage(data.toString()));
                } catch (IOException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        sessions.remove(sessionId);  // Hủy đăng ký observer
        sessionToChatbox.remove(sessionId);
    }
}
```

**Cấu hình WebSocket**

```java
// File: backend/src/main/java/com/flightmanagement/config/WebSocketConfig.java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Giao tiếp real-time**: Tin nhắn chat được broadcast đến tất cả observers (các client kết nối)
- **Decoupling**: Người gửi tin nhắn không biết về người nhận
- **Đăng ký động**: Client có thể kết nối/ngắt kết nối (subscribe/unsubscribe) tại runtime
- **Event-driven**: Hệ thống phản ứng với events (tin nhắn mới) bằng cách thông báo observers
- **Khả năng mở rộng**: Dễ dàng thêm observers mới mà không thay đổi notification logic

---

## 8. Chain of Responsibility Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Chain of Responsibility** - Chuyển yêu cầu dọc theo một chuỗi các đối tượng xử lý cho đến khi có một đối tượng xử lý nó.

### 💻 Triển Khai Trong Code

**Filter trong Security Chain**

```java
// File: backend/src/main/java/com/flightmanagement/security/JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Xử lý CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                // Validate và set authentication
                // ...
            } catch (Exception e) {
                // Xử lý error
            }
        }

        // Chuyển đến filter tiếp theo trong chain
        filterChain.doFilter(request, response);
    }
}
```

**Cấu hình Chain**

```java
// File: backend/src/main/java/com/flightmanagement/security/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Thêm JWT filter vào chain
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Pipeline xử lý request**: Mỗi filter xử lý request hoặc chuyển đến filter tiếp theo
- **Authentication flow**: JWT filter validate token trước khi request đến controllers
- **Tính linh hoạt**: Dễ dàng thêm/bớt filter mà không sửa đổi filter khác
- **Tách biệt trách nhiệm**: Mỗi filter xử lý một trách nhiệm cụ thể (CORS, JWT, etc.)
- **Kiểm soát thứ tự**: Filters thực thi theo thứ tự đã định (JWT trước authentication)

---

## 9. Template Method Pattern

### 🎯 Mẫu Thiết Kế Áp Dụng

**Template Method** - Định nghĩa khung (skeleton) của một thuật toán trong một phương thức, hoãn lại một số bước cho các class con.

### 💻 Triển Khai Trong Code

```java
// File: backend/src/main/java/com/flightmanagement/mapper/BaseMapper.java
public interface BaseMapper<E, D> {
    // Template methods - định nghĩa cấu trúc thuật toán
    D toDto(E entity);
    E toEntity(D dto);

    // Implementation mặc định cho collections sử dụng template methods
    default List<D> toDtoList(List<E> entityList) {
        if (entityList == null) return null;
        return entityList.stream()
                .map(this::toDto)  // Gọi implementation của class con
                .collect(Collectors.toList());
    }

    default List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(this::toEntity)  // Gọi implementation của class con
                .collect(Collectors.toList());
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Tái sử dụng code**: Logic chuyển đổi collection được chia sẻ cho tất cả mapper
- **Hành vi nhất quán**: Tất cả mapper xử lý list conversion theo cùng một cách
- **Khả năng mở rộng**: Concrete mapper chỉ cần implement logic chuyển đổi đặc thù cho entity
- **DRY Principle**: Loại bỏ code duplicate cho list conversion trong 17+ mapper classes

---

## 10. Aspect Oriented Programming

### 🎯 Mẫu Thiết Kế Áp Dụng

**Aspect Oriented Programming** - Không phải một design pattern mà là một paradigm.
Nó giúp tách các “mối quan tâm cắt ngang” (như logging, bảo mật, giao dịch) ra khỏi logic chính để code rõ ràng và dễ bảo trì hơn.

### 💻 Triển Khai Trong Code

**Custom Exceptions**

```java
// File: backend/src/main/java/com/flightmanagement/exception/ResourceNotFoundException.java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**Global Exception Handler**

```java
// File: backend/src/main/java/com/flightmanagement/exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            null,
            "NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(
            BadRequestException ex) {
        ApiResponse<?> response = new ApiResponse<>(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            null,
            "BAD_REQUEST"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((first, second) -> first + ", " + second)
            .orElse("Validation error");

        ApiResponse<?> apiResponse = new ApiResponse<>(
            HttpStatus.BAD_REQUEST,
            errorMessage,
            null,
            "VALIDATION_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
```

### 📝 Ý Nghĩa Trong Dự Án

- **Tập trung hóa**: Tất cả logic xử lý exception ở một nơi
- **Response nhất quán**: Tất cả errors trả về cùng format ApiResponse
- **Tách biệt trách nhiệm**: Controllers không cần try-catch blocks
- **Tích hợp AOP**: Spring tự động route exceptions đến handlers phù hợp
- **Code sạch**: Services throw exceptions, handler chuyển đổi thành HTTP responses

---

## 🎯 Lợi Ích Đạt Được

### 🔧 Khả Năng Bảo Trì (Maintainability)

- Tách biệt rõ ràng trách nhiệm giúp code dễ sửa đổi
- Logic tập trung (mappers, exception handlers) giảm code duplicate
- Pattern nhất quán trong toàn codebase giúp dễ navigation

### 📈 Khả Năng Mở Rộng (Scalability)

- Service layer có thể scale độc lập
- Repository abstraction cho phép chuyển đổi database implementation
- Observer pattern hỗ trợ thêm nhiều WebSocket clients

### ✅ Khả Năng Kiểm Thử (Testability)

- Dependency injection cho phép inject mock cho unit tests
- DTOs cho phép test business logic mà không cần database
- Service interfaces cho phép test controllers với mocked services

### 🔒 Bảo Mật (Security)

- DTO pattern ngăn expose các trường nhạy cảm của entity
- Chain of Responsibility validate authentication trước khi đến business logic
- Centralized exception handling ngăn information leakage

### ⚡ Hiệu Năng (Performance)

- Singleton pattern giảm overhead tạo objects
- Repository pattern cho phép query optimization ở một nơi
- Strategy pattern cho phép chọn thuật toán tối ưu

---

## 💡 Kết Luận

Hệ thống Quản lý Chuyến bay này thể hiện việc áp dụng chuyên nghiệp **những design patterns khác nhau** trong kiến trúc của nó. Mỗi pattern phục vụ một mục đích cụ thể:

### 📦 Creational Patterns (Nhóm Khởi tạo)

- **Singleton, Builder, Dependency Injection** - Quản lý việc tạo và khởi tạo objects

### 🏗️ Structural Patterns (Nhóm Cấu trúc)

- **Facade, Repository, Service Layer, DTO, Mapper** - Tổ chức kiến trúc code

### 🔄 Behavioral Patterns (Nhóm Hành vi)

- **Strategy, Observer, Chain of Responsibility, Template Method, Command** - Định nghĩa tương tác giữa objects

### 🎨 Architectural Patterns

- **Layered Architecture (MVC), Centralized Exception Handling** - Cấu trúc tổng thể hệ thống

Sự kết hợp của các patterns này tạo ra một hệ thống quản lý chuyến bay **robust, maintainable, và scalable** theo các best practices của ngành và tuân thủ các nguyên tắc SOLID.