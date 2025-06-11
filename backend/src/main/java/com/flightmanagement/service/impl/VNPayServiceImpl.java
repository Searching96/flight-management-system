package com.flightmanagement.service.impl;

import com.flightmanagement.config.VNPayConfig;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

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
    ;

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
            String hexPart;

            // Check if txnRef starts with exactly 6 digits (HHMMSS format)
            if (txnRef.length() > TIME_PREFIX_LENGTH && txnRef.substring(0, TIME_PREFIX_LENGTH).matches("\\d{6}")) {
                // New format: HHMMSS + hex
                hexPart = txnRef.substring(TIME_PREFIX_LENGTH);

                // Log the extracted parts for debugging
                String timePart = txnRef.substring(0, TIME_PREFIX_LENGTH);
                System.out.println("Extracted time: " + timePart + " (HH:mm:ss = " +
                        timePart.substring(0, 2) + ":" + timePart.substring(2, 4) + ":" + timePart.substring(4, 6) +
                        "), hex: " + hexPart);
            } else if (txnRef.contains("-")) {
                // Legacy format with dash: counter-hex
                String[] parts = txnRef.split("-", 2);
                if (parts.length > 1) {
                    hexPart = parts[1];
                } else {
                    hexPart = txnRef;
                }
            } else {
                // Old format: assume entire string is hex
                hexPart = txnRef;
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
                ticketService.payTicket(ticket.getTicketId());

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
            // Common request parameters
            Map<String, String> paramValues = new HashMap<>();
            paramValues.put("command", "refund");

            String txn_ref = HexFormat.of().formatHex(orderId.getBytes(StandardCharsets.UTF_8));
            paramValues.put("txnRef", txn_ref);

            paramValues.put("amount", String.valueOf(Long.parseLong(amount) * 100));
            paramValues.put("transDate", transDate);
            paramValues.put("user", user);
            paramValues.put("transType", transType);
            paramValues.put("orderInfo", "Hoan tien GD OrderId:" + orderId);

            // Build parameters for VNPay API
            ObjectNode params = buildCommonApiParams(paramValues, request);

            // Add refund-specific parameters
            params.put("vnp_TransactionType", transType);
            params.put("vnp_Amount", paramValues.get("amount"));
            params.put("vnp_TransactionDate", transDate);
            params.put("vnp_CreateBy", user);

            // Generate secure hash for refund
            String requestId = params.get("vnp_RequestId").asText();
            String version = params.get("vnp_Version").asText();
            String command = params.get("vnp_Command").asText();
            String tmnCode = params.get("vnp_TmnCode").asText();
            String txnRef = params.get("vnp_TxnRef").asText();
            String vnpAmount = params.get("vnp_Amount").asText();
            String transactionNo = ""; // Optional parameter
            String createBy = params.get("vnp_CreateBy").asText();
            String createDate = params.get("vnp_CreateDate").asText();
            String ipAddr = params.get("vnp_IpAddr").asText();
            String orderInfo = params.get("vnp_OrderInfo").asText();

            String hashData = String.join("|", requestId, version, command, tmnCode, transType, txnRef, vnpAmount,
                    transactionNo, transDate, createBy, createDate, ipAddr, orderInfo);

            String secureHash = VNPayConfig.generateHmacSHA512Signature(hashData);

            return makeVnpayApiRequest(params, secureHash);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
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