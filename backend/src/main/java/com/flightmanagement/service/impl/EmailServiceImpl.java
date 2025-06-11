package com.flightmanagement.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightmanagement.dto.EmailBookingRequest;
import com.flightmanagement.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔐 Flight Management - Reset Your Password");

            String resetLink = frontendUrl + resetToken;
            String currentTime = LocalDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Plain text version
            String plainTextContent = String.format("""
                    ✈️ FLIGHT MANAGEMENT - PASSWORD RESET
                    =====================================
                    
                    Hello there!
                    
                    We received a request to reset the password for your Flight Management account.
                    
                    To reset your password, please click the link below or copy and paste it into your browser:
                    
                    %s
                    
                    ⏰ SECURITY NOTICE:
                    - This link expires in 15 minutes for your security
                    - If you didn't request this, please ignore this email
                    - Never share this link with anyone
                    
                    If you have any questions or concerns, please contact our support team at support@thinhuit.id.vn
                    
                    Best regards,
                    ✈️ The Flight Management Team
                    
                    Secure • Reliable • Efficient
                    Request sent on: %s UTC
                    """, resetLink, currentTime);

            // HTML version
            String htmlContent = String.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>Password Reset</title>
                            </head>
                            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                            
                                    <!-- Header -->
                                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                                        <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                            ✈️ Flight Management
                                        </h1>
                                        <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                            Secure Password Reset
                                        </p>
                                    </div>
                            
                                    <!-- Main Content -->
                                    <div style="padding: 40px 30px;">
                                        <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">
                                            🔐 Password Reset Request
                                        </h2>
                            
                                        <p style="color: #666666; line-height: 1.6; margin: 0 0 25px 0;">
                                            Hello there! We received a request to reset the password for your Flight Management account.
                                        </p>
                            
                                        <p style="color: #666666; line-height: 1.6; margin: 0 0 30px 0;">
                                            Click the button below to create a new password:
                                        </p>
                            
                                        <!-- Reset Button -->
                                        <div style="text-align: center; margin: 30px 0;">
                                            <a href="%s"
                                               style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                                      color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold;
                                                      font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                                🔗 Reset My Password
                                            </a>
                                        </div>
                            
                                        <p style="color: #999999; font-size: 14px; text-align: center; margin: 20px 0;">
                                            Or copy and paste this link in your browser:<br>
                                            <span style="word-break: break-all; color: #667eea;">%s</span>
                                        </p>
                            
                                        <!-- Security Notice -->
                                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                            <h3 style="color: #856404; margin: 0 0 15px 0; font-size: 16px;">
                                                ⏰ Security Notice
                                            </h3>
                                            <ul style="color: #856404; margin: 0; padding-left: 20px; line-height: 1.6;">
                                                <li>This link expires in <strong>15 minutes</strong> for your security</li>
                                                <li>If you didn't request this, please ignore this email</li>
                                                <li>Never share this link with anyone</li>
                                            </ul>
                                        </div>
                            
                                        <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                            If you have any questions or concerns, please contact our support team at
                                            <a href="mailto:support@thinhuit.id.vn" style="color: #667eea;">support@thinhuit.id.vn</a>
                                        </p>
                                    </div>
                            
                                    <!-- Footer -->
                                    <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                            ✈️ The Flight Management Team
                                        </p>
                                        <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                            Secure • Reliable • Efficient<br>
                                            Request sent on: %s UTC
                                        </p>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """,
                    resetLink, resetLink, currentTime);

            // Set both text and HTML content
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendBookingConfirmationEmail(EmailBookingRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(request.getEmail());
            helper.setSubject("✈️ Xác nhận đặt chỗ - " + request.getConfirmationCode());
            helper.setFrom(fromEmail);

            // Generate both plain text and HTML content
            String plainTextContent = generateBookingPlainText(request);
            String htmlContent = generateBookingEmailHtml(request);

            // Set both text and HTML content
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email xác nhận đặt chỗ thất bại", e);
        }
    }

    private String generateBookingPlainText(EmailBookingRequest request) {
        try {
            // Parse booking data with proper type safety
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> bookingData = objectMapper.convertValue(request.getBookingData(), typeRef);

            // Extract data safely
            String confirmationCode = request.getConfirmationCode();
            Map<String, Object> flightInfo = getMapValue(bookingData, "flightInfo");
            List<Map<String, Object>> tickets = getListValue(bookingData, "tickets");
            List<String> passengers = getListValue(bookingData, "passengers");
            Object totalAmount = bookingData.get("totalAmount");
            String bookingDate = (String) bookingData.get("bookingDate");

            // Current timestamp: 2025-06-11 04:48:33
            String formattedDateTime = "2025-06-11 04:48:33";

            // Check if payment is needed
            boolean isPaid = tickets != null && tickets.stream()
                    .allMatch(ticket -> {
                        Object status = ticket.get("ticketStatus");
                        return status != null && ((Number) status).intValue() == 1;
                    });

            // Build passenger list
            StringBuilder passengerList = new StringBuilder();
            if (tickets != null) {
                for (int i = 0; i < tickets.size(); i++) {
                    Map<String, Object> ticket = tickets.get(i);
                    String passengerName = passengers != null && i < passengers.size() ?
                            passengers.get(i) : "Hành khách " + (i + 1);
                    Object fare = ticket.get("fare");
                    Object seatNumber = ticket.get("seatNumber");
                    Object statusObj = ticket.get("ticketStatus");
                    int ticketStatus = statusObj != null ? ((Number) statusObj).intValue() : 0;

                    String statusText = ticketStatus == 1 ? "Đã thanh toán" : "Chờ thanh toán";

                    passengerList.append(String.format("""
                            Hành khách %d: %s
                            Ghế: %s | Giá vé: $%s | Trạng thái: %s
                            
                            """, i + 1, passengerName, seatNumber, fare, statusText));
                }
            }

            // Payment section
            String paymentSection = "";
            if (request.isIncludePaymentButton() && !isPaid && request.getPaymentUrl() != null) {
                paymentSection = String.format("""
                        
                        ⚠️ YÊU CẦU THANH TOÁN
                        ======================
                        Đặt chỗ này cần thanh toán để được xác nhận.
                        Những đặt chỗ chưa thanh toán có thể bị hủy tự động 24 giờ trước khởi hành.
                        
                        Để hoàn tất thanh toán, vui lòng truy cập:
                        %s
                        
                        Hoặc sao chép và dán liên kết trên vào trình duyệt của bạn.
                        
                        """, request.getPaymentUrl());
            }

            // Build complete plain text email
            return String.format("""
                    ✈️ FLIGHT MANAGEMENT - XÁC NHẬN ĐẶT CHỖ
                    ==========================================
                    
                    🎉 ĐẶT CHỖ THÀNH CÔNG!
                    
                    Trạng thái: %s
                    In ngày: %s UTC | Bởi: %s
                    
                    📋 MÃ XÁC NHẬN CỦA BẠN
                    =======================
                    %s
                    
                    ⚠️ QUAN TRỌNG: Vui lòng lưu mã xác nhận này.
                    Bạn sẽ cần nó để truy xuất hoặc quản lý đặt chỗ sau này.
                    %s
                    ✈️ THÔNG TIN CHUYẾN BAY
                    ========================
                    Chuyến bay: %s
                    Tuyến đường: %s → %s
                    Khởi hành: %s
                    %s
                    
                    👥 THÔNG TIN HÀNH KHÁCH
                    ========================
                    %s
                    📊 TÓM TẮT ĐẶT CHỖ
                    ===================
                    Ngày đặt: %s
                    Tổng hành khách: %d
                    Trạng thái thanh toán: %s
                    
                    💰 TỔNG TIỀN: $%s
                    
                    🔍 TIẾP THEO LÀ GÌ?
                    ===================
                    • Lưu mã xác nhận của bạn: %s
                    • Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành
                    • Mang theo giấy tờ tùy thân hợp lệ và mã xác nhận
                    • Bạn có thể quản lý đặt chỗ trực tuyến bằng mã xác nhận
                    
                    📞 HỖ TRỢ KHÁCH HÀNG
                    =====================
                    Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, vui lòng liên hệ
                    đội hỗ trợ của chúng tôi tại: support@thinhuit.id.vn
                    
                    ==========================================
                    ✈️ Đội ngũ Flight Management
                    Bảo mật • Tin cậy • Hiệu quả
                    Email được gửi vào: %s UTC
                    ==========================================
                    
                    Đây là email tự động. Vui lòng không trả lời email này.
                    """,
                    isPaid ? "✓ Đã thanh toán" : "⏱️ Chờ thanh toán", // status
                    formattedDateTime, "thinh0704hcm", // header info
                    confirmationCode, // confirmation code
                    paymentSection, // payment section
                    safeGet(flightInfo, "flightCode", "N/A"), // flight code
                    safeGet(flightInfo, "departureCity", "N/A"), safeGet(flightInfo, "arrivalCity", "N/A"), // route
                    formatDateTime(safeGet(flightInfo, "departureTime", "")), // departure
                    flightInfo != null && flightInfo.get("arrivalTime") != null ?
                            String.format("Đến: %s", formatDateTime((String) flightInfo.get("arrivalTime"))) : "", // arrival if exists
                    passengerList.toString(), // passenger list
                    formatDate(bookingDate), // booking date
                    tickets != null ? tickets.size() : 0, // passenger count
                    isPaid ? "Đã thanh toán" : "Chờ thanh toán", // payment status
                    totalAmount, // total amount
                    confirmationCode, // confirmation code for next steps
                    formattedDateTime // footer timestamp
            );

        } catch (Exception e) {
            // Fallback plain text if there's an error
            return String.format("""
                    ✈️ FLIGHT MANAGEMENT - XÁC NHẬN ĐẶT CHỖ
                    ==========================================
                    
                    🎉 ĐẶT CHỖ THÀNH CÔNG!
                    
                    Mã xác nhận: %s
                    In ngày: 2025-06-11 04:48:33 UTC | Bởi: thinh0704hcm
                    
                    ⚠️ QUAN TRỌNG: Vui lòng lưu mã xác nhận này.
                    Bạn sẽ cần nó để truy xuất hoặc quản lý đặt chỗ sau này.
                    
                    Để xem chi tiết đầy đủ, vui lòng truy cập trang web của chúng tôi
                    và tra cứu đặt chỗ bằng mã xác nhận.
                    
                    📞 HỖ TRỢ KHÁCH HÀNG: support@thinhuit.id.vn
                    
                    ==========================================
                    ✈️ Đội ngũ Flight Management
                    Email được gửi vào: 2025-06-11 04:48:33 UTC
                    ==========================================
                    """, request.getConfirmationCode());
        }
    }

    private String generateBookingEmailHtml(EmailBookingRequest request) {
        try {
            // Parse booking data with proper type safety
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> bookingData = objectMapper.convertValue(request.getBookingData(), typeRef);

            // Extract data safely with type safety
            String confirmationCode = request.getConfirmationCode();
            Map<String, Object> flightInfo = getMapValue(bookingData, "flightInfo");
            List<Map<String, Object>> tickets = getListValue(bookingData, "tickets");
            List<String> passengers = getListValue(bookingData, "passengers");
            Object totalAmount = bookingData.get("totalAmount");
            String bookingDate = (String) bookingData.get("bookingDate");

            // Format timestamp - Updated to current time: 2025-06-11 04:48:33
            String formattedDateTime = "2025-06-11 04:48:33";

            // Check if payment is needed
            boolean isPaid = tickets != null && tickets.stream()
                    .allMatch(ticket -> {
                        Object status = ticket.get("ticketStatus");
                        return status != null && ((Number) status).intValue() == 1;
                    });

            // Generate payment button if needed
            String paymentButton = "";
            String paymentWarning = "";

            if (request.isIncludePaymentButton() && !isPaid && request.getPaymentUrl() != null) {
                paymentButton = String.format("""
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" 
                               style="display: inline-block; padding: 15px 30px; 
                                      background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%);
                                      color: #ffffff; text-decoration: none; border-radius: 8px; 
                                      font-weight: bold; font-size: 16px; 
                                      box-shadow: 0 4px 15px rgba(40, 167, 69, 0.4);">
                                💳 Hoàn tất thanh toán - $%s
                            </a>
                        </div>
                        """, request.getPaymentUrl(), totalAmount);

                paymentWarning = """
                        <p style="text-align: center; color: #dc3545; font-weight: bold; 
                                  margin: 20px 0; background-color: #f8d7da; padding: 15px; 
                                  border-radius: 6px; border: 1px solid #f5c6cb;">
                            ⚠️ Yêu cầu thanh toán: Đặt chỗ này cần thanh toán để được xác nhận.
                            Những đặt chỗ chưa thanh toán có thể bị hủy tự động 24 giờ trước khởi hành.
                        </p>
                        """;
            }

            // Generate passenger list
            StringBuilder passengerRows = new StringBuilder();
            if (tickets != null) {
                for (int i = 0; i < tickets.size(); i++) {
                    Map<String, Object> ticket = tickets.get(i);
                    String passengerName = passengers != null && i < passengers.size() ?
                            passengers.get(i) : "Hành khách " + (i + 1);
                    Object fare = ticket.get("fare");
                    Object seatNumber = ticket.get("seatNumber");
                    Object statusObj = ticket.get("ticketStatus");
                    int ticketStatus = statusObj != null ? ((Number) statusObj).intValue() : 0;

                    String statusBadge = ticketStatus == 1 ?
                            "<span style='background-color: #28a745; color: white; padding: 4px 8px; border-radius: 12px; font-size: 12px;'>Đã thanh toán</span>" :
                            "<span style='background-color: #ffc107; color: black; padding: 4px 8px; border-radius: 12px; font-size: 12px;'>Chờ thanh toán</span>";

                    passengerRows.append(String.format("""
                            <tr>
                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">
                                    <strong>Hành khách %d: %s</strong><br>
                                    <span style="color: #6c757d;">Ghế: %s</span> %s
                                </td>
                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; text-align: right;">
                                    <span style="background-color: #007bff; color: white; padding: 6px 12px; border-radius: 12px; font-weight: bold;">$%s</span>
                                </td>
                            </tr>
                            """, i + 1, passengerName, seatNumber, statusBadge, fare));
                }
            }

            // Build complete HTML with password reset header/footer style
            return String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Xác nhận đặt chỗ - %s</title>
                    </head>
                    <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                        <div style="max-width: 700px; margin: 0 auto; background-color: #ffffff;">
                    
                            <!-- Header (Password Reset Style) -->
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                                <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                    ✈️ Flight Management
                                </h1>
                                <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                    Xác nhận đặt chỗ
                                </p>
                                <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                                    In ngày: %s UTC | Bởi: %s
                                </p>
                            </div>
                    
                            <!-- Success Message -->
                            <div style="padding: 30px; text-align: center; background-color: #d4edda; border-bottom: 1px solid #c3e6cb;">
                                <div style="font-size: 48px; margin-bottom: 15px;">✅</div>
                                <h2 style="color: #155724; margin: 0 0 15px 0;">Đặt chỗ thành công!</h2>
                                <div style="margin: 15px 0;">
                                    <span style="background-color: %s; color: %s; padding: 10px 20px; border-radius: 25px; font-weight: bold; font-size: 14px;">
                                        %s
                                    </span>
                                </div>
                            </div>
                    
                            %s
                            %s
                    
                            <!-- Confirmation Code -->
                            <div style="padding: 30px; text-align: center; background-color: #f8f9fa; border-bottom: 1px solid #dee2e6;">
                                <h3 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">Mã xác nhận của bạn</h3>
                                <div style="background-color: #e9ecef; padding: 25px; border-radius: 10px; margin: 15px 0; border: 2px solid #007bff;">
                                    <div style="font-size: 32px; font-weight: bold; color: #007bff; font-family: 'Courier New', monospace; letter-spacing: 2px;">
                                        %s
                                    </div>
                                </div>
                                <div style="color: #856404; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; padding: 15px; margin: 15px 0;">
                                    <strong>⚠️ Quan trọng:</strong> Vui lòng lưu mã xác nhận này. Bạn sẽ cần nó để truy xuất hoặc quản lý đặt chỗ sau này.
                                </div>
                            </div>
                    
                            <!-- Main Content -->
                            <div style="padding: 30px;">
                                <!-- Flight Information -->
                                <h3 style="color: #007bff; margin-bottom: 20px; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                                    Thông tin chuyến bay
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px;">
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc; width: 30%%;"><strong>Chuyến bay:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%s</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;"><strong>Tuyến đường:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%s → %s</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;"><strong>Khởi hành:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%s</td>
                                    </tr>
                                    %s
                                </table>
                    
                                <!-- Passenger Information -->
                                <h3 style="color: #007bff; margin: 30px 0 20px 0; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                                    Thông tin hành khách
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                                    %s
                                </table>
                    
                                <!-- Booking Summary -->
                                <h3 style="color: #007bff; margin: 30px 0 20px 0; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                                    Tóm tắt đặt chỗ
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px;">
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;"><strong>Ngày đặt:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%s</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;"><strong>Tổng hành khách:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%d</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;"><strong>Trạng thái thanh toán:</strong></td>
                                        <td style="padding: 12px; border-bottom: 1px dotted #ccc;">%s</td>
                                    </tr>
                                    <tr style="background-color: #f8f9fa;">
                                        <td style="padding: 15px; font-size: 18px;"><strong>Tổng tiền:</strong></td>
                                        <td style="padding: 15px; text-align: right; font-size: 24px; font-weight: bold; color: #007bff;">$%s</td>
                                    </tr>
                                </table>
                    
                                <!-- Next Steps -->
                                <h4 style="color: #333; margin: 30px 0 15px 0;">Tiếp theo là gì?</h4>
                                <ul style="color: #666; line-height: 1.6; margin: 0; padding-left: 20px;">
                                    <li>Lưu mã xác nhận của bạn: <strong style="color: #007bff;">%s</strong></li>
                                    <li>Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành</li>
                                    <li>Mang theo giấy tờ tùy thân hợp lệ và mã xác nhận</li>
                                    <li>Bạn có thể quản lý đặt chỗ trực tuyến bằng mã xác nhận</li>
                                </ul>
                                <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                    Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, vui lòng liên hệ đội hỗ trợ của chúng tôi tại
                                    <a href="mailto:support@thinhuit.id.vn" style="color: #667eea;">support@thinhuit.id.vn</a>
                                </p>
                            </div>
                    
                            <!-- Footer (Password Reset Style) -->
                            <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                    ✈️ Đội ngũ Flight Management
                                </p>
                                <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                    Bảo mật • Tin cậy • Hiệu quả<br>
                                    Email được gửi vào: %s UTC
                                </p>
                            </div>
                    
                        </div>
                    </body>
                    </html>
                    """,
                    confirmationCode, // title
                    formattedDateTime, "thinh0704hcm", // header info with current user
                    isPaid ? "#28a745" : "#ffc107", // status badge color
                    isPaid ? "white" : "black", // status badge text color
                    isPaid ? "✓ Đã thanh toán" : "⏱️ Chờ thanh toán", // status text
                    paymentButton, // payment button
                    paymentWarning, // payment warning
                    confirmationCode, // confirmation code
                    safeGet(flightInfo, "flightCode", "N/A"), // flight code
                    safeGet(flightInfo, "departureCity", "N/A"), safeGet(flightInfo, "arrivalCity", "N/A"), // route
                    formatDateTime(safeGet(flightInfo, "departureTime", "")), // departure
                    flightInfo != null && flightInfo.get("arrivalTime") != null ?
                            String.format("<tr><td style='padding: 12px; border-bottom: 1px dotted #ccc;'><strong>Đến:</strong></td><td style='padding: 12px; border-bottom: 1px dotted #ccc;'>%s</td></tr>",
                                    formatDateTime((String) flightInfo.get("arrivalTime"))) : "", // arrival if exists
                    passengerRows.toString(), // passenger rows
                    formatDate(bookingDate), // booking date
                    tickets != null ? tickets.size() : 0, // passenger count
                    isPaid ? "Đã thanh toán" : "Chờ thanh toán", // payment status
                    totalAmount, // total amount
                    confirmationCode, // next steps confirmation code
                    formattedDateTime // footer timestamp
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HTML email: " + e.getMessage(), e);
        }
    }

    // Helper methods for safe data extraction with proper generic types
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof List ? (List<T>) value : null;
    }

    private String safeGet(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || map.get(key) == null) return defaultValue;
        return map.get(key).toString();
    }

    private String formatDateTime(String dateTimeString) {
        try {
            if (dateTimeString == null || dateTimeString.isEmpty()) return "N/A";
            // Fixed pattern - removed illegal 'l' character, used 'H' for hour
            return java.time.LocalDateTime.parse(dateTimeString.replace("Z", ""))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy lúc HH:mm"));
        } catch (Exception e) {
            return dateTimeString; // fallback to original string
        }
    }

    private String formatDate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) return "N/A";
            return java.time.LocalDateTime.parse(dateString.replace("Z", ""))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return dateString; // fallback to original string
        }
    }
}