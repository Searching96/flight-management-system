package com.flightmanagement.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    
    /**
     * Creates a payment URL for the specified amount
     * @param confirmationCode The payment amount
     * @param bankCode The bank code (optional)
     * @param language The language (optional)
     * @param request The HTTP request
     * @return A map containing the payment URL and additional information
     */
    Map<String, Object> createPayment(String confirmationCode, String bankCode, String language, HttpServletRequest request);
    
    /**
     * Process the payment return from VNPay
     * @param request The HTTP request containing the VNPay return parameters
     * @return A map containing the payment result
     */
    Map<String, Object> processPaymentReturn(HttpServletRequest request);

    public Map<String, String> processIPN(HttpServletRequest request);
    
    /**
     * Query a transaction status
     * @param orderId The order ID
     * @param transDate The transaction date
     * @param request The HTTP request
     * @return A map containing the transaction status
     */
    Map<String, Object> queryTransaction(String orderId, String transDate, HttpServletRequest request);
    
    /**
     * Request a refund for a transaction
     * @param orderId The order ID
     * @param amount The refund amount
     * @param transDate The transaction date
     * @param user The user requesting the refund
     * @param transType The transaction type
     * @param request The HTTP request
     * @return A map containing the refund result
     */
    Map<String, Object> refundTransaction(String orderId, String amount, String transDate, 
                                        String user, String transType, HttpServletRequest request);
}
