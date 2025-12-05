package com.flightmanagement.service;

import com.flightmanagement.config.MoMoConfig;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.service.impl.MoMoServiceImpl;
import com.mservice.config.Environment;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.test.context.TestPropertySource;

/**
 * Test class for MoMo Payment Service - Core function for Payment Processing
 * 
 * Available Tags:
 * - createPayment: Tests for creating payment URLs and processing payment requests
 * - processPaymentReturn: Tests for processing MoMo payment return callbacks
 * - processIPN: Tests for processing MoMo IPN (Instant Payment Notification)
 * - queryTransaction: Tests for querying payment transaction status
 * - refundTransaction: Tests for refunding payment transactions
 * - errorHandling: Tests for various error scenarios
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "momo.partner-code=MOMOLRJZ20181206",
    "momo.access-key=mTCKt9W3eU1m39TW",
    "momo.secret-key=SetA5RDnLHvt51AULf51DyauxUo3kDU6",
    "momo.endpoint=https://test-payment.momo.vn/v2/gateway/api",
    "momo.return-url=http://localhost:3000/payment-result",
    "momo.notify-url=http://localhost:8080/api/payment/IPN",
    "momo.environment=dev"
})
public class MoMoPaymentServiceTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private PassengerService passengerService;

    @Mock
    private FlightService flightService;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private MoMoServiceImpl paymentService;

    private MockedStatic<MoMoConfig> mockedMoMoConfig;
    private MockedStatic<CreateOrderMoMo> mockedCreateOrderMoMo;
    private MockedStatic<Environment> mockedEnvironment;

    private TicketDto testTicket;
    private TicketDto testTicket2;
    private PassengerDto testPassenger;
    private FlightDto testFlight;
    private List<TicketDto> testTickets;

    @BeforeEach
    void setUp() {
        // Mock MoMoConfig static methods
        mockedMoMoConfig = mockStatic(MoMoConfig.class);
        mockedCreateOrderMoMo = mockStatic(CreateOrderMoMo.class);
        mockedEnvironment = mockStatic(Environment.class);

        mockedMoMoConfig.when(MoMoConfig::getUniqueOrderId).thenReturn("1637825472123");
        mockedMoMoConfig.when(MoMoConfig::getUniqueRequestId).thenReturn("20251122000001");
        mockedMoMoConfig.when(() -> MoMoConfig.generateHmacSHA256Signature(anyString()))
                .thenReturn("mockedSignature");

        // Mock environment
        Environment mockEnv = mock(Environment.class);
        mockedMoMoConfig.when(MoMoConfig::getMoMoEnvironment).thenReturn(mockEnv);
        mockedEnvironment.when(() -> Environment.selectEnv(anyString())).thenReturn(mockEnv);

        // Setup test data
        setupTestData();
    }

    private void setupTestData() {
        // Setup test ticket
        testTicket = new TicketDto();
        testTicket.setTicketId(1);
        testTicket.setConfirmationCode("ABC123");
        testTicket.setFare(new BigDecimal("1000000")); // 1,000,000 VND
        testTicket.setTicketStatus((byte)0); // Unpaid
        testTicket.setPaymentTime(null);
        testTicket.setPassengerId(1);
        testTicket.setFlightId(1);

        testTicket2 = new TicketDto();
        testTicket2.setTicketId(2);
        testTicket2.setConfirmationCode("ABC123");
        testTicket2.setFare(new BigDecimal("500000")); // 500,000 VND
        testTicket2.setTicketStatus((byte)0); // Unpaid
        testTicket2.setPaymentTime(null);
        testTicket2.setPassengerId(2);
        testTicket2.setFlightId(1);

        testTickets = Arrays.asList(testTicket, testTicket2);

        // Setup test passenger
        testPassenger = new PassengerDto();
        testPassenger.setPassengerId(1);
        testPassenger.setPassengerName("Test User");
        testPassenger.setEmail("test@example.com");

        // Setup test flight
        testFlight = new FlightDto();
        testFlight.setFlightId(1);
        testFlight.setFlightCode("VN123");
    }

    @AfterEach
    void tearDown() {
        if (mockedMoMoConfig != null) {
            mockedMoMoConfig.close();
        }
        if (mockedCreateOrderMoMo != null) {
            mockedCreateOrderMoMo.close();
        }
        if (mockedEnvironment != null) {
            mockedEnvironment.close();
        }
    }

    // ================ CREATE PAYMENT TESTS ================

    @Test
    @Tag("createPayment")
    void testCreatePayment_Success_ReturnsMoMoPaymentUrl() {
        // Given
        when(ticketService.getTicketsOnConfirmationCode("ABC123")).thenReturn(testTickets);

        PaymentResponse mockResponse = new PaymentResponse(0, "success");
        mockResponse.setPayUrl("https://test-payment.momo.vn/gw_payment/gateway?token=abc123");
        mockResponse.setQrCodeUrl("https://test-payment.momo.vn/qr/abc123");
        mockResponse.setDeeplink("momo://payment?token=abc123");

        mockedCreateOrderMoMo.when(() -> CreateOrderMoMo.process(
            any(), anyString(), anyString(), anyString(), anyString(), 
            anyString(), anyString(), anyString(), any(), any()
        )).thenReturn(mockResponse);

        // When
        Map<String, Object> result = paymentService.createPayment("ABC123", null, "vn", httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("payment"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> payment = (Map<String, Object>) result.get("payment");
        assertEquals("00", payment.get("code"));
        assertEquals("success", payment.get("message"));
        assertNotNull(payment.get("data")); // Payment URL
        assertNotNull(payment.get("qrCodeUrl"));
        assertNotNull(payment.get("deeplink"));
    }

    @Test
    @Tag("createPayment")
    void testCreatePayment_NoUnpaidTickets_ReturnsError() {
        // Given
        TicketDto paidTicket = new TicketDto();
        paidTicket.setTicketStatus((byte)1); // Already paid
        paidTicket.setPaymentTime(LocalDateTime.now());
        
        when(ticketService.getTicketsOnConfirmationCode("ABC123"))
                .thenReturn(Arrays.asList(paidTicket));

        // When
        Map<String, Object> result = paymentService.createPayment("ABC123", null, "vn", httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("payment"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> payment = (Map<String, Object>) result.get("payment");
        assertEquals("01", payment.get("code"));
        assertTrue(payment.get("message").toString().contains("No unpaid tickets"));
    }

    @Test
    @Tag("createPayment")
    void testCreatePayment_MoMoError_ReturnsError() {
        // Given
        when(ticketService.getTicketsOnConfirmationCode("ABC123")).thenReturn(testTickets);

        PaymentResponse mockResponse = new PaymentResponse(4006, "Transaction failed");

        mockedCreateOrderMoMo.when(() -> CreateOrderMoMo.process(
            any(), anyString(), anyString(), anyString(), anyString(), 
            anyString(), anyString(), anyString(), any(), any()
        )).thenReturn(mockResponse);

        // When
        Map<String, Object> result = paymentService.createPayment("ABC123", null, "vn", httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("payment"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> payment = (Map<String, Object>) result.get("payment");
        assertEquals("01", payment.get("code"));
        assertTrue(payment.get("message").toString().contains("MoMo payment creation failed"));
    }

    // ================ PROCESS PAYMENT RETURN TESTS ================

    @Test
    @Tag("processPaymentReturn")
    void testProcessPaymentReturn_Success_ReturnsSuccessResponse() {
        // Given
        setupMoMoReturnParameters(httpRequest, "0", "success");
        when(ticketService.getTicketsOnConfirmationCode("ABC123")).thenReturn(testTickets);

        // When
        Map<String, Object> result = paymentService.processPaymentReturn(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("0", result.get("resultCode"));
        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Giao dịch thành công", result.get("description"));
    }

    @Test
    @Tag("processPaymentReturn")
    void testProcessPaymentReturn_Failed_ReturnsFailureResponse() {
        // Given
        setupMoMoReturnParameters(httpRequest, "4006", "Transaction failed");

        // When
        Map<String, Object> result = paymentService.processPaymentReturn(httpRequest);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("4006", result.get("resultCode"));
        assertEquals("FAILED", result.get("status"));
        assertTrue(result.get("description").toString().contains("Giao dịch thất bại"));
    }

    // ================ PROCESS IPN TESTS ================

    @Test
    @Tag("processIPN")
    void testProcessIPN_ValidSignature_Success_ReturnsConfirmSuccess() {
        // Given
        setupMoMoIPNParameters(httpRequest, "0", "success", "mockedSignature");
        when(ticketService.getTicketsOnConfirmationCode("ABC123")).thenReturn(testTickets);

        // When
        Map<String, String> result = paymentService.processIPN(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals("00", result.get("RspCode"));
        assertEquals("Confirm Success", result.get("Message"));
    }

    @Test
    @Tag("processIPN")
    void testProcessIPN_InvalidSignature_ReturnsInvalidChecksum() {
        // Given
        setupMoMoIPNParameters(httpRequest, "0", "success", "invalidSignature");

        // When
        Map<String, String> result = paymentService.processIPN(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals("97", result.get("RspCode"));
        assertEquals("Invalid Checksum", result.get("Message"));
    }

    @Test
    @Tag("processIPN")
    void testProcessIPN_MissingParameters_ReturnsError() {
        // Given - Missing required parameters
        when(httpRequest.getParameter("orderId")).thenReturn(null);
        when(httpRequest.getParameter("requestId")).thenReturn("20251122000001");

        // When
        Map<String, String> result = paymentService.processIPN(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals("97", result.get("RspCode"));
        assertEquals("Invalid Checksum", result.get("Message"));
    }

    // ================ HELPER METHODS ================

    private void setupMoMoReturnParameters(HttpServletRequest request, String resultCode, String message) {
        when(request.getParameter("partnerCode")).thenReturn("MOMOLRJZ20181206");
        when(request.getParameter("orderId")).thenReturn("1637825472123");
        when(request.getParameter("requestId")).thenReturn("20251122000001");
        when(request.getParameter("amount")).thenReturn("1500000");
        when(request.getParameter("orderInfo")).thenReturn("Thanh toan ve may bay. Ma don hang: ABC123");
        when(request.getParameter("orderType")).thenReturn("momo_wallet");
        when(request.getParameter("transId")).thenReturn("123456789");
        when(request.getParameter("resultCode")).thenReturn(resultCode);
        when(request.getParameter("message")).thenReturn(message);
        when(request.getParameter("payType")).thenReturn("qr");
        when(request.getParameter("responseTime")).thenReturn("1637825472000");
        when(request.getParameter("extraData")).thenReturn("");
        when(request.getParameter("signature")).thenReturn("mockedSignature");
    }

    private void setupMoMoIPNParameters(HttpServletRequest request, String resultCode, String message, String signature) {
        when(request.getParameter("partnerCode")).thenReturn("MOMOLRJZ20181206");
        when(request.getParameter("orderId")).thenReturn("1637825472123");
        when(request.getParameter("requestId")).thenReturn("20251122000001");
        when(request.getParameter("amount")).thenReturn("1500000");
        when(request.getParameter("orderInfo")).thenReturn("Thanh toan ve may bay. Ma don hang: ABC123");
        when(request.getParameter("orderType")).thenReturn("momo_wallet");
        when(request.getParameter("transId")).thenReturn("123456789");
        when(request.getParameter("resultCode")).thenReturn(resultCode);
        when(request.getParameter("message")).thenReturn(message);
        when(request.getParameter("payType")).thenReturn("qr");
        when(request.getParameter("responseTime")).thenReturn("1637825472000");
        when(request.getParameter("extraData")).thenReturn("");
        when(request.getParameter("signature")).thenReturn(signature);
    }
}