package com.flightmanagement.service.impl;

import com.flightmanagement.config.MoMoConfig;
import com.flightmanagement.dto.FlightDto;
import com.flightmanagement.dto.PassengerDto;
import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.service.*;
import com.mservice.config.Environment;
import com.mservice.enums.RequestType;
import com.mservice.models.PaymentResponse;
import com.mservice.processor.CreateOrderMoMo;
import com.mservice.processor.QueryTransactionStatus;
import com.mservice.processor.RefundTransaction;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MoMoServiceImpl implements PaymentService {

    private final TicketService ticketService;
    private final PassengerService passengerService;
    private final FlightService flightService;
    private final EmailService emailService;

    public MoMoServiceImpl(TicketService ticketService, PassengerService passengerService,
                          FlightService flightService, EmailService emailService) {
        this.ticketService = ticketService;
        this.passengerService = passengerService;
        this.flightService = flightService;
        this.emailService = emailService;
    }

    @Override
    public Map<String, Object> createPayment(String confirmationCode, String bankCode, String language,
                                             HttpServletRequest request) {
        try {
            // Calculate total amount for unpaid tickets
            BigDecimal totalAmount = ticketService.getTicketsOnConfirmationCode(confirmationCode)
                    .stream()
                    .filter(ticketDto -> ticketDto.getTicketStatus() == 0 && ticketDto.getPaymentTime() == null)
                    .map(TicketDto::getFare)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Validate amount
            if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("No unpaid tickets found or ticket already paid");
            }

            // Convert to MoMo format (VND, no decimal places)
            long amount = totalAmount.longValue();

            // Generate unique identifiers
            String orderId = MoMoConfig.getUniqueOrderId();
            String requestId = MoMoConfig.getUniqueRequestId();

            // Format order info
            String orderInfo = removeAccent("Thanh toan ve may bay. Ma don hang: " + confirmationCode);

            // Get MoMo environment
            Environment environment = MoMoConfig.getMoMoEnvironment();

            // Create payment request through MoMo
            PaymentResponse momoResponse = CreateOrderMoMo.process(
                environment,
                orderId,
                requestId,
                String.valueOf(amount),
                orderInfo,
                MoMoConfig.getReturnUrl(),
                MoMoConfig.getNotifyUrl(),
                "", // extraData
                RequestType.CAPTURE_WALLET,
                Boolean.TRUE
            );

            if (momoResponse != null && momoResponse.getResultCode() == 0) {
                // Success response
                Map<String, Object> response = new HashMap<>();
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("code", "00");
                responseData.put("message", "success");
                responseData.put("data", momoResponse.getPayUrl());
                responseData.put("orderInfo", orderInfo);
                responseData.put("orderCode", orderId);
                responseData.put("requestId", requestId);
                responseData.put("amount", amount);
                responseData.put("qrCodeUrl", momoResponse.getQrCodeUrl());
                responseData.put("deeplink", momoResponse.getDeeplink());
                response.put("payment", responseData);

                // Log payment creation
                System.out.println("MoMo payment created at " + LocalDateTime.now() + " for confirmation: " + confirmationCode);
                System.out.println("Order ID: " + orderId + ", Request ID: " + requestId);

                return response;
            } else {
                String errorMsg = momoResponse != null ? momoResponse.getMessage() : "Unknown error from MoMo";
                return createErrorResponse("MoMo payment creation failed: " + errorMsg);
            }

        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> processPaymentReturn(HttpServletRequest request) {
        try {
            // Extract parameters from MoMo callback
            String partnerCode = request.getParameter("partnerCode");
            String orderId = request.getParameter("orderId");
            String requestId = request.getParameter("requestId");
            String amount = request.getParameter("amount");
            String orderInfo = request.getParameter("orderInfo");
            String orderType = request.getParameter("orderType");
            String transId = request.getParameter("transId");
            String resultCode = request.getParameter("resultCode");
            String message = request.getParameter("message");
            String payType = request.getParameter("payType");
            String responseTime = request.getParameter("responseTime");
            String extraData = request.getParameter("extraData");
            String signature = request.getParameter("signature");

            // Validate signature
            String rawHash = "accessKey=" + MoMoConfig.getAccessKey() +
                           "&amount=" + amount +
                           "&extraData=" + (extraData != null ? extraData : "") +
                           "&message=" + message +
                           "&orderId=" + orderId +
                           "&orderInfo=" + orderInfo +
                           "&orderType=" + orderType +
                           "&partnerCode=" + partnerCode +
                           "&payType=" + payType +
                           "&requestId=" + requestId +
                           "&responseTime=" + responseTime +
                           "&resultCode=" + resultCode +
                           "&transId=" + transId;

            String computedSignature = MoMoConfig.generateHmacSHA256Signature(rawHash);

            Map<String, Object> result = new HashMap<>();
            result.put("success", "0".equals(resultCode));
            result.put("resultCode", resultCode);
            result.put("message", message);
            result.put("orderId", orderId);
            result.put("requestId", requestId);
            result.put("amount", amount);
            result.put("transId", transId);
            result.put("payType", payType);
            result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("signatureValid", signature != null && signature.equals(computedSignature));

            // Process payment based on result code
            if ("0".equals(resultCode)) {
                // Payment successful - update tickets and send confirmation
                String confirmationCode = extractConfirmationCodeFromOrderInfo(orderInfo);
                if (confirmationCode != null) {
                    updateTicketsAfterPayment(confirmationCode, orderId, transId);
                }
                result.put("status", "SUCCESS");
                result.put("description", "Giao dịch thành công");
            } else {
                result.put("status", "FAILED");
                result.put("description", getErrorDescription(resultCode));
            }

            return result;

        } catch (Exception e) {
            return createErrorResponse("Error processing MoMo callback: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> processIPN(HttpServletRequest request) {
        try {
            // Extract parameters from MoMo IPN
            String partnerCode = request.getParameter("partnerCode");
            String orderId = request.getParameter("orderId");
            String requestId = request.getParameter("requestId");
            String amount = request.getParameter("amount");
            String orderInfo = request.getParameter("orderInfo");
            String orderType = request.getParameter("orderType");
            String transId = request.getParameter("transId");
            String resultCode = request.getParameter("resultCode");
            String message = request.getParameter("message");
            String payType = request.getParameter("payType");
            String responseTime = request.getParameter("responseTime");
            String extraData = request.getParameter("extraData");
            String signature = request.getParameter("signature");

            // Validate required parameters
            if (orderId == null || requestId == null || resultCode == null || signature == null) {
                Map<String, String> response = new HashMap<>();
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            // Validate signature
            String rawHash = "accessKey=" + MoMoConfig.getAccessKey() +
                           "&amount=" + amount +
                           "&extraData=" + (extraData != null ? extraData : "") +
                           "&message=" + message +
                           "&orderId=" + orderId +
                           "&orderInfo=" + orderInfo +
                           "&orderType=" + orderType +
                           "&partnerCode=" + partnerCode +
                           "&payType=" + payType +
                           "&requestId=" + requestId +
                           "&responseTime=" + responseTime +
                           "&resultCode=" + resultCode +
                           "&transId=" + transId;

            String computedSignature = MoMoConfig.generateHmacSHA256Signature(rawHash);

            Map<String, String> response = new HashMap<>();

            if (signature.equals(computedSignature)) {
                if ("0".equals(resultCode)) {
                    // Payment successful
                    String confirmationCode = extractConfirmationCodeFromOrderInfo(orderInfo);
                    if (confirmationCode != null) {
                        updateTicketsAfterPayment(confirmationCode, orderId, transId);
                    }
                    response.put("RspCode", "00");
                    response.put("Message", "Confirm Success");
                } else {
                    // Payment failed
                    response.put("RspCode", "01");
                    response.put("Message", "Payment Failed");
                }
            } else {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
            }

            return response;

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("RspCode", "99");
            response.put("Message", "Internal Server Error: " + e.getMessage());
            return response;
        }
    }

    @Override
    public Map<String, Object> queryTransaction(String orderId, String transDate, HttpServletRequest request) {
        try {
            Environment environment = MoMoConfig.getMoMoEnvironment();
            String requestId = MoMoConfig.getUniqueRequestId();

            // Query transaction through MoMo
            var queryResponse = QueryTransactionStatus.process(environment, orderId, requestId);

            Map<String, Object> result = new HashMap<>();
            if (queryResponse != null) {
                result.put("resultCode", queryResponse.getResultCode());
                result.put("message", queryResponse.getMessage());
                result.put("orderId", orderId);
                result.put("requestId", requestId);
                result.put("transId", queryResponse.getTransId());
                result.put("amount", queryResponse.getAmount());
                result.put("success", queryResponse.getResultCode() == 0);
            } else {
                result.put("resultCode", "99");
                result.put("message", "Query failed");
                result.put("success", false);
            }

            return result;

        } catch (Exception e) {
            return createErrorResponse("Error querying transaction: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> refundTransaction(String orderId, String amount, String transDate, 
                                                String user, String transType, HttpServletRequest request) {
        try {
            Environment environment = MoMoConfig.getMoMoEnvironment();
            String requestId = MoMoConfig.getUniqueRequestId();

            // Parse amount
            Long refundAmount = Long.parseLong(amount);
            Long transactionNo = Long.parseLong(transType); // Use transType as transaction number
            
            // Create refund request through MoMo
            var refundResponse = RefundTransaction.process(
                environment,
                orderId,
                requestId,
                String.valueOf(refundAmount),
                transactionNo,
                "Refund by: " + user
            );

            Map<String, Object> result = new HashMap<>();
            if (refundResponse != null) {
                result.put("resultCode", refundResponse.getResultCode());
                result.put("message", refundResponse.getMessage());
                result.put("orderId", orderId);
                result.put("requestId", requestId);
                result.put("transId", refundResponse.getTransId());
                result.put("amount", refundAmount);
                result.put("success", refundResponse.getResultCode() == 0);
            } else {
                result.put("resultCode", "99");
                result.put("message", "Refund failed");
                result.put("success", false);
            }

            return result;

        } catch (Exception e) {
            return createErrorResponse("Error processing refund: " + e.getMessage());
        }
    }

    /**
     * Update ticket status and payment information after successful payment
     */
    private void updateTicketsAfterPayment(String confirmationCode, String orderId, String transId) {
        try {
            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);
            
            // Pay all tickets in the booking together
            for (TicketDto ticket : tickets) {
                if (ticket.getTicketStatus() == 0) {
                    // Pay the ticket using the existing payTicket method
                    ticketService.payTicket(ticket.getTicketId(), orderId);
                }
            }

            // Send confirmation email for the entire booking
            if (!tickets.isEmpty()) {
                sendBookingConfirmationEmail(tickets, orderId, transId);
            }

            System.out.println("Booking payment completed for " + tickets.size() + " tickets with confirmation code: " + confirmationCode);

        } catch (Exception e) {
            System.err.println("Error updating tickets after payment: " + e.getMessage());
        }
    }

    /**
     * Send booking confirmation email for the entire booking
     */
    private void sendBookingConfirmationEmail(List<TicketDto> tickets, String orderId, String transId) {
        try {
            // Group tickets by passenger for cleaner email structure
            Map<Integer, List<TicketDto>> ticketsByPassenger = tickets.stream()
                    .collect(java.util.stream.Collectors.groupingBy(TicketDto::getPassengerId));

            for (Map.Entry<Integer, List<TicketDto>> entry : ticketsByPassenger.entrySet()) {
                try {
                    PassengerDto passenger = passengerService.getPassengerById(entry.getKey());
                    
                    if (passenger != null && !entry.getValue().isEmpty()) {
                        TicketDto firstTicket = entry.getValue().get(0);
                        FlightDto flight = flightService.getFlightById(firstTicket.getFlightId());

                        if (flight != null) {
                            // Calculate total fare for this passenger's tickets
                            BigDecimal totalFare = entry.getValue().stream()
                                    .map(TicketDto::getFare)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            // Get seat numbers for this passenger
                            String seatNumbers = entry.getValue().stream()
                                    .map(ticket -> ticket.getSeatNumber() != null ? ticket.getSeatNumber() : "TBD")
                                    .collect(java.util.stream.Collectors.joining(", "));

                            emailService.sendSingleTicketConfirmation(
                                passenger.getEmail(),
                                "Customer", // Could be enhanced with actual customer name
                                passenger.getPassengerName(),
                                firstTicket.getConfirmationCode(),
                                flight.getFlightCode(),
                                flight.getDepartureCityName() != null ? flight.getDepartureCityName() : "Unknown",
                                flight.getArrivalCityName() != null ? flight.getArrivalCityName() : "Unknown",
                                flight.getDepartureTime() != null ? flight.getDepartureTime().toString() : "Unknown",
                                seatNumbers,
                                totalFare,
                                true // Payment completed for entire booking
                            );
                            
                            System.out.println("Booking confirmation email sent for " + entry.getValue().size() + 
                                             " tickets to: " + passenger.getEmail() + " (Order: " + orderId + ")");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error sending email for passenger " + entry.getKey() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending booking confirmation emails: " + e.getMessage());
        }
    }

    /**
     * Extract confirmation code from order info
     */
    private String extractConfirmationCodeFromOrderInfo(String orderInfo) {
        if (orderInfo != null && orderInfo.contains("Ma don hang:")) {
            String[] parts = orderInfo.split("Ma don hang:");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return null;
    }

    /**
     * Remove accents from Vietnamese text
     */
    private String removeAccent(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", "01");
        responseData.put("message", "Error: " + errorMessage);
        response.put("payment", responseData);
        return response;
    }

    /**
     * Get error description based on MoMo result code
     */
    private String getErrorDescription(String resultCode) {
        switch (resultCode != null ? resultCode : "") {
            case "0":
                return "Giao dịch thành công";
            case "9000":
                return "Giao dịch được duyệt thành công";
            case "8000":
                return "Giao dịch đang được xử lý";
            case "1000":
                return "Giao dịch được khởi tạo thành công";
            case "4001":
                return "Số tiền vượt quá hạn mức thanh toán";
            case "4006":
                return "Giao dịch thất bại";
            case "4007":
                return "Trừ tiền thành công. Giao dịch bị hủy do không hoàn thành trong thời gian quy định";
            case "4010":
                return "Giao dịch thất bại do tài khoản người dùng chưa được kích hoạt";
            case "4015":
                return "Giao dịch thất bại do OTP không chính xác";
            case "4016":
                return "Giao dịch thất bại do OTP hết hạn";
            case "4017":
                return "Giao dịch thất bại do nhập sai OTP quá số lần quy định";
            case "5001":
                return "Giao dịch thất bại do địa chỉ IP bị từ chối";
            case "5002":
                return "Giao dịch thất bại do sai phương thức thanh toán";
            case "5003":
                return "Giao dịch thất bại do sai mã đối tác";
            case "5004":
                return "Giao dịch thất bại do sai định dạng dữ liệu";
            case "5005":
                return "Giao dịch thất bại do sai mã hash";
            case "5006":
                return "Giao dịch thất bại do sai mã xác thực";
            default:
                return "Lỗi không xác định";
        }
    }

    @Override
    public Map<String, Object> getPaymentStatus(String confirmationCode) {
        try {
            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);
            
            if (tickets.isEmpty()) {
                return createErrorResponse("No booking found for confirmation code: " + confirmationCode);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("confirmationCode", confirmationCode);
            result.put("totalTickets", tickets.size());
            
            // Check if the entire booking is paid (all tickets paid or all tickets unpaid)
            long paidTickets = tickets.stream().filter(t -> t.getTicketStatus() == 1).count();
            long unpaidTickets = tickets.stream().filter(t -> t.getTicketStatus() == 0).count();
            
            boolean bookingPaid = paidTickets == tickets.size();
            boolean partiallyPaid = paidTickets > 0 && unpaidTickets > 0;
            
            result.put("paidTickets", paidTickets);
            result.put("unpaidTickets", unpaidTickets);
            result.put("bookingPaid", bookingPaid);
            result.put("partiallyPaid", partiallyPaid);
            
            // Calculate total amounts
            BigDecimal totalAmount = tickets.stream()
                    .map(TicketDto::getFare)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal paidAmount = bookingPaid ? totalAmount : BigDecimal.ZERO;
            BigDecimal unpaidAmount = bookingPaid ? BigDecimal.ZERO : totalAmount;
            
            result.put("totalAmount", totalAmount);
            result.put("paidAmount", paidAmount);
            result.put("unpaidAmount", unpaidAmount);
            result.put("success", true);
            result.put("paymentRequired", !bookingPaid);
            
            return result;
            
        } catch (Exception e) {
            return createErrorResponse("Error getting payment status: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> cancelPayment(String confirmationCode, HttpServletRequest request) {
        try {
            List<TicketDto> tickets = ticketService.getTicketsOnConfirmationCode(confirmationCode);
            
            if (tickets.isEmpty()) {
                return createErrorResponse("No booking found for confirmation code: " + confirmationCode);
            }
            
            // Check if the entire booking is already paid
            boolean anyTicketPaid = tickets.stream().anyMatch(t -> t.getTicketStatus() == 1);
            if (anyTicketPaid) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "Cannot cancel booking - payment has already been completed. Use refund instead.");
                result.put("requiresRefund", true);
                result.put("bookingStatus", "paid");
                return result;
            }
            
            // Cancel all tickets in the booking
            int cancelledCount = 0;
            for (TicketDto ticket : tickets) {
                try {
                    ticketService.cancelTicket(ticket.getTicketId());
                    cancelledCount++;
                } catch (Exception e) {
                    System.err.println("Error cancelling ticket " + ticket.getTicketId() + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Booking cancelled successfully");
            result.put("confirmationCode", confirmationCode);
            result.put("cancelledTickets", cancelledCount);
            result.put("bookingStatus", "cancelled");
            result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            return result;
            
        } catch (Exception e) {
            return createErrorResponse("Error cancelling booking: " + e.getMessage());
        }
    }
}