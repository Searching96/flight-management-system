package com.flightmanagement.controller;

import com.flightmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestParam("amount") @NotBlank(message = "Amount is required")
            @Pattern(regexp = "^[0-9]+$", message = "Amount must be a number") String amountParam,

            @RequestParam(value = "bankCode", required = false) String bankCode,

            @RequestParam(value = "language", required = false)
            @Pattern(regexp = "^(vn|en)$", message = "Language must be either 'vn' or 'en'") String language,

            HttpServletRequest request) {

        Map<String, Object> response = paymentService.createPayment(amountParam, bankCode, language, request);

        return ResponseEntity.ok(response);
    }

    // Both IPN and return endpoints process the payment callback from VNPay
    @GetMapping("/ipn")
    public ResponseEntity<Map<String, Object>> ipn(HttpServletRequest request) {
        Map<String, Object> response = paymentService.processPaymentReturn(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/return")
    public ResponseEntity<Map<String, Object>> processReturn(HttpServletRequest request) {
        Map<String, Object> response = paymentService.processPaymentReturn(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryTransaction(
            @RequestParam("orderId") String orderId,
            @RequestParam("transDate") String transDate,
            HttpServletRequest request) {

        Map<String, Object> response = paymentService.queryTransaction(orderId, transDate, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundTransaction(
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") String amount,
            @RequestParam("transDate") String transDate,
            @RequestParam("user") String user,
            @RequestParam(value = "transType", defaultValue = "02") String transType,
            HttpServletRequest request) {

        Map<String, Object> response = paymentService.refundTransaction(
                orderId, amount, transDate, user, transType, request);
        return ResponseEntity.ok(response);
    }
}