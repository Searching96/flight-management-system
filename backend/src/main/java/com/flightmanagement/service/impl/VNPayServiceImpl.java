package com.flightmanagement.service.impl;

import com.flightmanagement.config.VNPayConfig;
import com.flightmanagement.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class VNPayServiceImpl implements PaymentService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String VERSION = "2.1.0";
    private static final String TIME_ZONE = "Etc/GMT+7";
    private static final String CURRENCY_CODE = "VND";

    @Override
    public Map<String, Object> createPayment(String amountParam, String bankCode, String language, HttpServletRequest request) {
        try {
            // Generate unique transaction reference (must be unique per day)
            String txnRef = VNPayConfig.getRandomNumber(8);
            String ipAddr = VNPayConfig.getIpAddress(request);
            
            // Format orderInfo to match VNPAY requirements (no special characters, no accented Vietnamese)
            String rawOrderInfo = "Thanh toan ve may bay. Ma don hang:" + txnRef;
            String orderInfo = removeAccent(rawOrderInfo);
            
            SimpleDateFormat formatter = createDateFormatter();
            Calendar calendar = createCalendar();
            String createDate = formatter.format(calendar.getTime());
            
            // Calculate amount (must multiply by 100 to remove decimal points as per VNPAY requirements)
            long amount;
            try {
                amount = Long.parseLong(amountParam) * 100;
                if (amount <= 0) {
                    throw new IllegalArgumentException("Invalid payment amount");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid payment amount format");
            }
            
            // Build parameters map according to VNPAY API documentation
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", VERSION);             // Required: API version
            params.put("vnp_Command", "pay");               // Required: payment command
            params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode); // Required: Terminal ID
            params.put("vnp_Amount", String.valueOf(amount)); // Required: Amount (×100, no decimals)
            params.put("vnp_CurrCode", CURRENCY_CODE);      // Required: Currency (VND only)
            params.put("vnp_TxnRef", txnRef);               // Required: Unique transaction reference
            params.put("vnp_OrderInfo", orderInfo);         // Required: Order description
            params.put("vnp_OrderType", "190000");          // Required: Order type code (airline tickets)
            params.put("vnp_Locale", language != null && !language.isEmpty() ? language : "vn"); // Required: Language
            params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl); // Required: Return URL
            params.put("vnp_IpAddr", ipAddr);               // Required: Customer IP
            params.put("vnp_CreateDate", createDate);       // Required: Create date (GMT+7)
            
            // Add bank code if provided (optional parameter)
            if (bankCode != null && !bankCode.isEmpty()) {
                params.put("vnp_BankCode", bankCode);       // Optional: Banking method
            }
            
            // Add expiration date (15 minutes from now)
            calendar.add(Calendar.MINUTE, 15);
            params.put("vnp_ExpireDate", formatter.format(calendar.getTime())); // Required: Expiration time

            // Generate payment URL with secure hash
            String paymentUrl = buildPaymentUrl(params);

            // Format response
            return formatCreatePaymentResponse(paymentUrl, orderInfo, txnRef);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> processPaymentReturn(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fields = new HashMap<>();
        
        // Extract parameters from request
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Verify signature
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayConfig.hashAllFields(fields);
        boolean checkSignature = signValue.equals(vnp_SecureHash);
        
        // Format response
        String responseCode = fields.get("vnp_ResponseCode");
        response.put("code", responseCode);
        response.put("message", getResponseMessage(responseCode));
        response.put("data", fields);
        response.put("signatureValid", checkSignature);
        
        return response;
    }

    @Override
    public Map<String, Object> queryTransaction(String orderId, String transDate, HttpServletRequest request) {
        try {
            // Common request parameters
            Map<String, String> paramValues = new HashMap<>();
            paramValues.put("command", "querydr");
            paramValues.put("txnRef", orderId);
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
            
            String hashData = String.join("|", requestId, version, command, tmnCode, txnRef, 
                    transDate, createDate, ipAddr, orderInfo);
            String secureHash = VNPayConfig.generateHmacSHA512Signature(hashData);
            
            return makeVnpayApiRequest(params, secureHash);
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> refundTransaction(String orderId, String amount, String transDate, 
                                               String user, String transType, HttpServletRequest request) {
        try {
            // Common request parameters
            Map<String, String> paramValues = new HashMap<>();
            paramValues.put("command", "refund");
            paramValues.put("txnRef", orderId);
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
            
            String hashData = String.join("|", requestId, version, command, tmnCode, 
                    transType, txnRef, vnpAmount, transactionNo, transDate, 
                    createBy, createDate, ipAddr, orderInfo);
            
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
        String requestId = VNPayConfig.getRandomNumber(8);
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

    /**
     * Builds the payment URL for redirect-based payment
     */
    private String buildPaymentUrl(Map<String, String> vnp_Params) {
        // Sort field names (required for correct checksum calculation)
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        // Add secure hash to query string
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.generateHmacSHA512Signature(hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    /**
     * Remove diacritical marks (accents) from Vietnamese text
     * VNPAY requires text without accents and special characters
     */
    private String removeAccent(String text) {
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.replaceAll("[^\\p{ASCII}]", "");
        return temp.replaceAll("[^a-zA-Z0-9. ]", " ");
    }

    /**
     * Makes an API request to VNPay with the provided parameters and secure hash
     */
    private Map<String, Object> makeVnpayApiRequest(ObjectNode vnp_Params, String vnp_SecureHash) {
        try {
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
            
            // Make API call to VNPay
            URL url = new URL(VNPayConfig.vnp_ApiUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
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

    /**
     * Creates a standard date formatter in the expected VNPay format
     */
    private SimpleDateFormat createDateFormatter() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    /**
     * Creates a calendar with the VNPay expected timezone
     */
    private Calendar createCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
    }

    /**
     * Processes HTTP connection response
     */
    private static Map<String, Object> getResponse(HttpURLConnection con) throws IOException {
        //int responseCode = con.getResponseCode();
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

    /**
     * Creates a formatted response for payment creation
     */
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

    /**
     * Creates a standardized error response
     */
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
