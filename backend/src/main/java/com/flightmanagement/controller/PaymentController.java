package com.flightmanagement.controller;

import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "Operations related to payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create a payment")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPayment(
            @RequestParam("confirmationCode") @NotBlank(message = "Confirmation code is required")
            String confirmationCodeParam,

            @RequestParam(value = "bankCode", required = false) String bankCode,

            @RequestParam(value = "language", required = false)
            @Pattern(regexp = "^(vn|en)$", message = "Language must be either 'vn' or 'en'") String language,

            HttpServletRequest request) {

        Map<String, Object> response = paymentService.createPayment(confirmationCodeParam, bankCode, language, request);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Payment created successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Process IPN callback")
    @GetMapping("/IPN")
    public ResponseEntity<ApiResponse<Map<String, String>>> ipn(HttpServletRequest request) {
        Map<String, String> response = paymentService.processIPN(request);
        
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "IPN processed successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Process return callback")
    @GetMapping("/return")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processReturn(HttpServletRequest request) {
        Map<String, Object> response = paymentService.processPaymentReturn(request);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Return processed successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<Map<String, Object>>> queryTransaction(
            @RequestParam("orderId") String orderId,
            @RequestParam("transDate") String transDate,
            HttpServletRequest request) {

        Map<String, Object> response = paymentService.queryTransaction(orderId, transDate, request);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Transaction query completed successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("refund")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refundTransaction(
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") String amount,
            @RequestParam("transDate") String transDate,
            @RequestParam("user") String user,
            @RequestParam(value = "transType", defaultValue = "02") String transType,
            HttpServletRequest request) {

        Map<String, Object> response = paymentService.refundTransaction(
                orderId, amount, transDate, user, transType, request);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Refund transaction completed successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get payment status for confirmation code")
    @GetMapping("/status/{confirmationCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentStatus(
            @PathVariable("confirmationCode") @NotBlank(message = "Confirmation code is required")
            String confirmationCode) {

        Map<String, Object> response = paymentService.getPaymentStatus(confirmationCode);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Payment status retrieved successfully",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Cancel payment for confirmation code")
    @PostMapping("/cancel/{confirmationCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelPayment(
            @PathVariable("confirmationCode") @NotBlank(message = "Confirmation code is required")
            String confirmationCode,
            HttpServletRequest request) {

        Map<String, Object> response = paymentService.cancelPayment(confirmationCode, request);
        
        ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Payment cancellation completed",
                response,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}