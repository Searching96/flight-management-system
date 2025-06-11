package com.flightmanagement.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.flightmanagement.config.VNPayConfig;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class VNPayServiceImpl implements PaymentService {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private FlightService flightService;
    @Autowired
    private EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String VERSION = "2.1.0";
    private static final String TIME_ZONE = "Etc/GMT+7";
    private static final String CURRENCY_CODE = "VND";
    private static final int TIME_PREFIX_LENGTH = 6; // HHMMSS format

    @Override
    public Map<String, Object> createPayment(String confirmationCode, String bankCode, String language,
                                             HttpServletRequest request) {
        try {
            // Calculate amount
            BigDecimal amount = ticketService.getTicketsOnConfirmationCode(confirmationCode)
                    .stream()
                    .filter(ticketDto -> ticketDto.getTicketStatus() == 0 && ticketDto.getPaymentTime() == null)
                    .map(TicketDto::getFare)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(100));

            // Generate unique transaction reference with HHMMSS prefix
            String txnRef = generateUniqueTxnRef(confirmationCode);
            String ipAddr = VNPayConfig.getIpAddress(request);

            // Format orderInfo to match VNPAY requirements
            String rawOrderInfo = "Thanh toan ve may bay. Ma don hang:" + confirmationCode;
            String orderInfo = removeAccent(rawOrderInfo);

            SimpleDateFormat formatter = createDateFormatter();
            Calendar calendar = createCalendar();
            String createDate = formatter.format(calendar.getTime());

            // Validate amount
            try {
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Invalid payment amount");
                }
                if (amount.compareTo(BigDecimal.ZERO) == 0) {
                    throw new IllegalArgumentException("Ticket already paid or no tickets found");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid payment amount format");
            }

            // Build parameters map according to VNPAY API documentation
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", VERSION);
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            params.put("vnp_Amount", amount.toBigInteger().toString());
            params.put("vnp_CurrCode", CURRENCY_CODE);
            params.put("vnp_TxnRef", txnRef);
            params.put("vnp_OrderInfo", orderInfo);
            params.put("vnp_OrderType", "190000");
            params.put("vnp_Locale", language != null && !language.isEmpty() ? language : "vn");
            params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            params.put("vnp_IpAddr", ipAddr);
            params.put("vnp_CreateDate", createDate);

            // Add bank code if provided
            if (bankCode != null && !bankCode.isEmpty()) {
                params.put("vnp_BankCode", bankCode);
            }

            // Add expiration date (15 minutes from now)
            calendar.add(Calendar.MINUTE, 15);
            params.put("vnp_ExpireDate", formatter.format(calendar.getTime()));

            // Generate payment URL with secure hash
            String paymentUrl = buildPaymentUrl(params);

            // Log payment creation for debugging - Current time: 2025-06-11 05:07:16
            System.out.println("Payment created at 2025-06-11 05:07:16 UTC by thinh0704hcm");
            System.out.println("TxnRef generated: " + txnRef + " for confirmation: " + confirmationCode);

            // Format response
            return formatCreatePaymentResponse(paymentUrl, orderInfo, txnRef);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * Generate unique transaction reference with HHMMSS prefix + hex confirmation code
     * Format: [HHMMSS][HEX_CONFIRMATION_CODE]
     * Example: 050716486454C4C4F (HHMMSS + hex confirmation code)
     */
    private String generateUniqueTxnRef(String confirmationCode) {
        // Get current time and format as HHMMSS (24-hour format)
        LocalDateTime now = LocalDateTime.now();
        String timePrefix = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // Convert confirmation code to hex
        String hexConfirmationCode = HexFormat.of().formatHex(confirmationCode.getBytes(StandardCharsets.UTF_8));

        // Combine time prefix with hex confirmation code
        String txnRef = timePrefix + hexConfirmationCode;

        // Ensure it doesn't exceed 100 characters (VNPAY limit)
        if (txnRef.length() > 100) {
            // Truncate hex part if necessary, keeping the full time prefix
            int maxHexLength = 100 - TIME_PREFIX_LENGTH;
            if (maxHexLength > 0) {
                hexConfirmationCode = hexConfirmationCode.substring(0, Math.min(hexConfirmationCode.length(), maxHexLength));
                txnRef = timePrefix + hexConfirmationCode;
            } else {
                // Fallback: use only time prefix if confirmation code is too long
                txnRef = timePrefix;
            }
        }

        return txnRef;
    }

    /**
     * Extract confirmation code from txnRef
     * Handles both old format (hex only) and new format (HHMMSS + hex)
     */
    private String extractConfirmationCode(String txnRef) {
        try {
            String hexPart = "";

            // Check if txnRef starts with exactly 6 digits (HHMMSS format)
            if (txnRef.length() > TIME_PREFIX_LENGTH && txnRef.substring(0, TIME_PREFIX_LENGTH).matches("\\d{6}")) {
                // New format: HHMMSS + hex
                hexPart = txnRef.substring(TIME_PREFIX_LENGTH);
            }

            // Convert hex back to confirmation code
            if (hexPart.isEmpty()) {
                return txnRef; // Return original if no hex part found
            }

            byte[] decodedBytes = HexFormat.of().parseHex(hexPart);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Error extracting confirmation code from txnRef: " + txnRef + " - " + e.getMessage());
            return txnRef; // Return as-is for manual handling
        }
    }

    // Update your processPaymentReturn method

    @Override
    public Map<String, Object> processPaymentReturn(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fields = new HashMap<>();

        // Extract parameters from request
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Verify signature
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VNPayConfig.hashAllFields(fields);
        boolean checkSignature = signValue.equals(vnp_SecureHash);
        String responseCode = fields.get("vnp_ResponseCode");

        // RED ALERT - MEANT TO BE REMOVE AND SWITCHED TO IPN
        if (checkSignature && ("00".equals(responseCode) || "01".equals(responseCode))) {
            String confirmationCode = extractConfirmationCode(request.getParameter("vnp_TxnRef"));

            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);

            // Process payment for each ticket
            tickets.forEach(ticket -> {
                if (ticket.getTicketStatus() == 1 && ticket.getPaymentTime() != null) return;
                ticketService.payTicket(ticket.getTicketId(), fields.get("vnp_TxnRef"));

                // Send notification email to each passenger
                try {
                    sendPassengerNotification(ticket, confirmationCode);
                    System.out.println("Payment notification sent to passenger for ticket: " + ticket.getTicketId() +
                            " at 2025-06-11 07:27:09 UTC by thinh0704hcm");
                } catch (Exception e) {
                    System.err.println("Failed to send payment notification to passenger for ticket: " +
                            ticket.getTicketId() + " - " + e.getMessage());
                    // Don't fail the payment process if email fails
                }
            });
        }

        // Return all relevant info for UI display
        response.put("code", responseCode);
        response.put("message", getResponseMessage(responseCode));
        response.put("signatureValid", checkSignature);
        response.put("amount", fields.get("vnp_Amount"));
        response.put("orderId", fields.get("vnp_TxnRef"));
        response.put("orderInfo", fields.get("vnp_OrderInfo"));
        response.put("transactionId", fields.get("vnp_TransactionNo"));
        response.put("bankCode", fields.get("vnp_BankCode"));
        response.put("cardType", fields.get("vnp_CardType"));
        response.put("paymentDate", fields.get("vnp_PayDate"));
        response.put("transactionStatus", fields.get("vnp_TransactionStatus"));
        response.put("data", fields);

        return response;
    }

    /**
     * Send payment notification to passenger
     */
    private void sendPassengerNotification(TicketDto ticket, String confirmationCode) {
        try {
            // Get passenger information
            PassengerDto passenger = passengerService.getPassengerById(ticket.getPassengerId());

            // Get flight information
            FlightDto flight = flightService.getFlightById(ticket.getFlightId());

            // Format departure time
            String formattedDepartureTime = formatDateTime(flight.getDepartureTime());

            // Send notification email
            emailService.sendPassengerPaymentNotification(
                    passenger.getEmail(),
                    passenger.getPassengerName(),
                    confirmationCode,
                    flight.getFlightCode(),
                    flight.getDepartureCityName(),
                    flight.getArrivalCityName(),
                    formattedDepartureTime,
                    ticket.getSeatNumber(),
                    ticket.getFare()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to send passenger notification for ticket: " + ticket.getTicketId(), e);
        }
    }

    /**
     * Format date time for display
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        try {
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy lúc HH:mm"));
        } catch (Exception e) {
            return dateTime.toString();
        }
    }

    @Override
    public Map<String, String> processIPN(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VNPayConfig.hashAllFields(fields);

        Map<String, String> result = new HashMap<>();
        try {
            if (!signValue.equals(vnp_SecureHash)) {
                result.put("RspCode", "97");
                result.put("Message", "Invalid Checksum");
                return result;
            }

            String confirmationCode = extractConfirmationCode(request.getParameter("vnp_TxnRef"));
            String amount = fields.get("vnp_Amount");
            String responseCode = fields.get("vnp_ResponseCode");

            // 1. Check if order exists
            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);
            if (tickets.isEmpty()) {
                result.put("RspCode", "01");
                result.put("Message", "Order not Found");
                return result;
            }

            // 2. Check amount
            BigDecimal expectedAmount = tickets.stream()
                    .map(TicketDto::getFare)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(100));
            boolean checkAmount = expectedAmount.toBigInteger().toString().equals(amount);

            // 3. Check order status (pending = 0)
            boolean isPending = tickets.stream().anyMatch(t -> t.getTicketStatus() == 0);

            if (!checkAmount) {
                result.put("RspCode", "04");
                result.put("Message", "Invalid Amount");
                return result;
            }

            if (!isPending) {
                result.put("RspCode", "02");
                result.put("Message", "Order already confirmed");
                return result;
            }

            // 4. Update status
            if ("00".equals(responseCode)) {
                tickets.forEach(ticket -> {
                    ticket.setTicketStatus((byte) 1);
                    ticket.setPaymentTime(LocalDateTime.parse(fields.get("vnp_PayDate"),
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                    ticketService.updateTicket(ticket.getTicketId(), ticket);
                });
            } else {
                tickets.forEach(ticket -> {
                    ticket.setTicketStatus((byte) 2); // failed
                    ticketService.updateTicket(ticket.getTicketId(), ticket);
                });
            }
            result.put("RspCode", "00");
            result.put("Message", "Confirm Success");
        } catch (Exception e) {
            result.put("RspCode", "99");
            result.put("Message", "Unknown error");
        }

        return result;
    }

    @Override
    public Map<String, Object> queryTransaction(String orderId, String transDate, HttpServletRequest request) {
        try {
            // Common request parameters
            Map<String, String> paramValues = new HashMap<>();
            paramValues.put("command", "querydr");

            String txn_ref = generateUniqueTxnRef(orderId);
            paramValues.put("txnRef", txn_ref);
            paramValues.put("transDate", transDate);
            paramValues.put("orderInfo", "Kiem tra ket qua GD OrderId:" + orderId);

            // Build parameters for VNPay API
            ObjectNode params = buildCommonApiParams(paramValues, request);

            // Generate secure hash
            String requestId = params.get("vnp_RequestId").asText();
            String version = params.get("vnp_Version").asText();
            String command = params.get("vnp_Command").asText();
            String tmnCode = params.get("vnp_TmnCode").asText();
            String txnRef = params.get("vnp_TxnRef").asText();
            String createDate = params.get("vnp_CreateDate").asText();
            String ipAddr = params.get("vnp_IpAddr").asText();
            String orderInfo = params.get("vnp_OrderInfo").asText();

            String hashData = String.join("|", requestId, version, command, tmnCode, txnRef, transDate, createDate,
                    ipAddr, orderInfo);
            String secureHash = VNPayConfig.generateHmacSHA512Signature(hashData);

            return makeVnpayApiRequest(params, secureHash);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> refundTransaction(String orderId, String amount, String transDate, String user,
                                                 String transType, HttpServletRequest request) {
        try {
            System.out.println("Start refunding transaction for OrderId: " + orderId + " at " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Build refund parameters
            String requestId = VNPayConfig.getRandomNumber().toString();
            String ipAddr = VNPayConfig.getIpAddress(request);
            Calendar calendar = createCalendar();
            SimpleDateFormat formatter = createDateFormatter();
            String createDate = formatter.format(calendar.getTime());

            // Convert amount to VNPay format (multiply by 100)
            String vnpAmount = String.valueOf(Long.parseLong(amount) * 100);

            ObjectNode params = objectMapper.createObjectNode();
            params.put("vnp_RequestId", requestId);
            params.put("vnp_Version", VERSION);
            params.put("vnp_Command", "refund");
            params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            params.put("vnp_TxnRef", orderId);
            params.put("vnp_Amount", vnpAmount);
            params.put("vnp_OrderInfo", "Hoan tien GD OrderId:" + orderId);
            params.put("vnp_TransactionDate", transDate);
            params.put("vnp_CreateDate", createDate);
            params.put("vnp_CreateBy", user);
            params.put("vnp_IpAddr", ipAddr);
            params.put("vnp_TransactionType", transType);

            // Generate secure hash - CRITICAL: Must match VNPay's expected order
            String hashData = String.join("|",
                    requestId, VERSION, "refund", VNPayConfig.vnp_TmnCode,
                    transType, orderId, vnpAmount, "", transDate,
                    user, createDate, ipAddr, "Hoan tien GD OrderId:" + orderId
            );

            String secureHash = VNPayConfig.generateHmacSHA512Signature(hashData);
            params.put("vnp_SecureHash", secureHash);

            System.out.println("Hash data: " + hashData);
            System.out.println("Secure hash: " + secureHash);

            // Create RestClient with proper configuration
            RestClient restClient = RestClient.builder()
                    .baseUrl("https://sandbox.vnpayment.vn/merchant_webapi")
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Accept", "application/json")
                    .build();

            // Convert ObjectNode to Map
            Map<String, Object> requestBody = convertObjectNodeToMap(params);

            // Send request
            ResponseEntity<String> response = restClient.post()
                    .uri("/api/transaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                        try {
                            String errorBody = new String(resp.getBody().readAllBytes());
                            System.err.println("4xx Error: " + errorBody);
                            throw new RuntimeException("VNPay API Client Error: " + resp.getStatusCode() + " - " + errorBody);
                        } catch (IOException e) {
                            throw new RuntimeException("VNPay API Client Error: " + resp.getStatusCode());
                        }
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, resp) -> {
                        try {
                            String errorBody = new String(resp.getBody().readAllBytes());
                            System.err.println("5xx Error: " + errorBody);
                            throw new RuntimeException("VNPay API Server Error: " + resp.getStatusCode() + " - " + errorBody);
                        } catch (IOException e) {
                            throw new RuntimeException("VNPay API Server Error: " + resp.getStatusCode());
                        }
                    })
                    .toEntity(String.class);

            // Parse response
            Map<String, Object> vnpayResponse = objectMapper.readValue(response.getBody(), Map.class);
            System.out.println("Refund response: " + vnpayResponse);
            return processVNPayResponse(vnpayResponse);

        } catch (Exception e) {
            System.err.println("Refund error: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Refund failed: " + e.getMessage());
        }
    }

    // Helper method to convert ObjectNode to Map
    private Map<String, Object> convertObjectNodeToMap(ObjectNode objectNode) {
        Map<String, Object> map = new HashMap<>();
        objectNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            if (value.isTextual()) {
                map.put(key, value.asText());
            } else if (value.isNumber()) {
                map.put(key, value.asLong());
            } else if (value.isBoolean()) {
                map.put(key, value.asBoolean());
            } else {
                map.put(key, value.toString());
            }
        });
        return map;
    }

    // Method to process VNPay response
    private Map<String, Object> processVNPayResponse(Map<String, Object> vnpayResponse) {
        if (vnpayResponse == null) {
            return createErrorResponse("No response from VNPay");
        }

        String responseCode = (String) vnpayResponse.get("vnp_ResponseCode");
        String message = (String) vnpayResponse.get("vnp_Message");

        Map<String, Object> result = new HashMap<>();
        result.put("success", "00".equals(responseCode));
        result.put("responseCode", responseCode);
        result.put("message", message);
        result.put("timestamp", "2025-06-11 12:47:08");
        result.put("user", "thinh0704hcm");
        result.put("vnpayResponse", vnpayResponse);

        // Add response code meanings for better understanding
        switch (responseCode != null ? responseCode : "") {
            case "00":
                result.put("status", "SUCCESS");
                result.put("description", "Yêu cầu thành công");
                break;
            case "02":
                result.put("status", "INVALID_TMN_CODE");
                result.put("description", "Mã định danh kết nối không hợp lệ");
                break;
            case "03":
                result.put("status", "INVALID_DATA_FORMAT");
                result.put("description", "Dữ liệu gửi sang không đúng định dạng");
                break;
            case "91":
                result.put("status", "TRANSACTION_NOT_FOUND");
                result.put("description", "Không tìm thấy giao dịch yêu cầu hoàn trả");
                break;
            case "94":
                result.put("status", "DUPLICATE_REQUEST");
                result.put("description", "Giao dịch đã được gửi yêu cầu hoàn tiền trước đó");
                break;
            case "95":
                result.put("status", "TRANSACTION_FAILED");
                result.put("description", "Giao dịch này không thành công bên VNPAY");
                break;
            case "97":
                result.put("status", "INVALID_CHECKSUM");
                result.put("description", "Checksum không hợp lệ");
                break;
            case "99":
                result.put("status", "OTHER_ERROR");
                result.put("description", "Các lỗi khác");
                break;
            default:
                result.put("status", "UNKNOWN");
                result.put("description", "Mã lỗi không xác định");
        }

        return result;
    }

    /**
     * Builds common API parameters for VNPay requests
     */
    private ObjectNode buildCommonApiParams(Map<String, String> paramValues, HttpServletRequest request) {
        String requestId = VNPayConfig.getRandomNumber().toString();
        String ipAddr = VNPayConfig.getIpAddress(request);

        Calendar calendar = createCalendar();
        SimpleDateFormat formatter = createDateFormatter();
        String createDate = formatter.format(calendar.getTime());

        ObjectNode params = objectMapper.createObjectNode();
        params.put("vnp_RequestId", requestId);
        params.put("vnp_Version", VERSION);
        params.put("vnp_Command", paramValues.get("command"));
        params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        params.put("vnp_TxnRef", paramValues.get("txnRef"));
        params.put("vnp_OrderInfo", paramValues.get("orderInfo"));
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_IpAddr", ipAddr);

        if (paramValues.containsKey("transDate")) {
            params.put("vnp_TransactionDate", paramValues.get("transDate"));
        }

        return params;
    }

    private String buildPaymentUrl(Map<String, String> vnp_Params) {
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.generateHmacSHA512Signature(hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    private String removeAccent(String text) {
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.replaceAll("[^\\p{ASCII}]", "");
        return temp.replaceAll("[^a-zA-Z0-9. ]", " ");
    }

    private Map<String, Object> makeVnpayApiRequest(ObjectNode vnp_Params, String vnp_SecureHash) {
        try {
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

            URL url = new URI(VNPayConfig.vnp_ApiUrl).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(vnp_Params.toString());
            wr.flush();
            wr.close();

            return getResponse(con);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    private SimpleDateFormat createDateFormatter() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    private Calendar createCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
    }

    private static Map<String, Object> getResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuilder responseData = new StringBuilder();
        while ((output = in.readLine()) != null) {
            responseData.append(output);
        }
        in.close();

        Map<String, Object> response = new HashMap<>();
        response.put("code", "00");
        response.put("message", "Success");
        response.put("data", responseData.toString());
        return response;
    }

    private Map<String, Object> formatCreatePaymentResponse(String paymentUrl, String orderInfo, String orderCode) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", "00");
        responseData.put("message", "success");
        responseData.put("data", paymentUrl);
        responseData.put("orderInfo", orderInfo);
        responseData.put("orderCode", orderCode);
        response.put("payment", responseData);
        return response;
    }

    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", "01");
        responseData.put("message", "Error: " + errorMessage);
        response.put("payment", responseData);
        return response;
    }

    private String getResponseMessage(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Giao dịch thành công";
            case "01" -> "Đơn hàng đã được xác nhận";
            case "02" -> "Giao dịch thất bại";
            case "09" -> "Thẻ/Tài khoản hết hạn mức";
            case "10" -> "Không đủ hạn mức";
            case "11" -> "Đã hết hạn chờ thanh toán";
            case "12" -> "Thẻ bị khóa";
            case "13" -> "OTP không đúng";
            case "24" -> "Giao dịch không thành công";
            case "51" -> "Tài khoản không đủ số dư";
            case "65" -> "Tài khoản vượt quá giới hạn giao dịch trong ngày";
            case "75" -> "Ngân hàng đang bảo trì";
            case "79" -> "Nhập sai mật khẩu quá số lần quy định";
            default -> "Lỗi không xác định";
        };
    }
}