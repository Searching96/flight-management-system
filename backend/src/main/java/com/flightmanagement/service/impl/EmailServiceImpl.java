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

import java.math.BigDecimal;
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
            helper.setSubject("🔐 Flight Management - Đặt lại mật khẩu");

            String resetLink = frontendUrl + resetToken;
            String currentTime = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Phiên bản văn bản thuần túy
            String plainTextContent = String.format("""
                    ✈️ FLIGHT MANAGEMENT - ĐẶT LẠI MẬT KHẨU
                    =========================================
                    
                    Xin chào!
                    
                    Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản Flight Management của bạn.
                    
                    Để đặt lại mật khẩu, vui lòng nhấp vào liên kết bên dưới hoặc sao chép và dán vào trình duyệt:
                    
                    %s
                    
                    ⏰ THÔNG BÁO BẢO MẬT:
                    - Liên kết này hết hạn sau 15 phút để đảm bảo an toàn
                    - Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này
                    - Không bao giờ chia sẻ liên kết này với bất kỳ ai
                    
                    Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, vui lòng liên hệ đội hỗ trợ của chúng tôi tại support@thinhuit.id.vn
                    
                    Trân trọng,
                    ✈️ Đội ngũ Flight Management
                    
                    Bảo mật • Tin cậy • Hiệu quả
                    Yêu cầu được gửi vào: %s UTC
                    """, resetLink, currentTime);

            // Phiên bản HTML
            String htmlContent = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Đặt lại mật khẩu</title>
                    </head>
                    <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                    
                            <!-- Tiêu đề -->
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                                <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                    ✈️ Flight Management
                                </h1>
                                <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                    Đặt lại mật khẩu an toàn
                                </p>
                            </div>
                    
                            <!-- Nội dung chính -->
                            <div style="padding: 40px 30px;">
                                <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">
                                    🔐 Yêu cầu đặt lại mật khẩu
                                </h2>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 0 0 25px 0;">
                                    Xin chào! Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản Flight Management của bạn.
                                </p>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 0 0 30px 0;">
                                    Nhấp vào nút bên dưới để tạo mật khẩu mới:
                                </p>
                    
                                <!-- Nút đặt lại -->
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s"
                                       style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                              color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold;
                                              font-size: 16px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);">
                                        🔗 Đặt lại mật khẩu
                                    </a>
                                </div>
                    
                                <p style="color: #999999; font-size: 14px; text-align: center; margin: 20px 0;">
                                    Hoặc sao chép và dán liên kết này vào trình duyệt:<br>
                                    <span style="word-break: break-all; color: #667eea;">%s</span>
                                </p>
                    
                                <!-- Thông báo bảo mật -->
                                <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                    <h3 style="color: #856404; margin: 0 0 15px 0; font-size: 16px;">
                                        ⏰ Thông báo bảo mật
                                    </h3>
                                    <ul style="color: #856404; margin: 0; padding-left: 20px; line-height: 1.6;">
                                        <li>Liên kết này hết hạn sau <strong>15 phút</strong> để đảm bảo an toàn</li>
                                        <li>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này</li>
                                        <li>Không bao giờ chia sẻ liên kết này với bất kỳ ai</li>
                                    </ul>
                                </div>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                    Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, vui lòng liên hệ đội hỗ trợ của chúng tôi tại
                                    <a href="mailto:support@thinhuit.id.vn" style="color: #667eea;">support@thinhuit.id.vn</a>
                                </p>
                            </div>
                    
                            <!-- Chân trang -->
                            <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                    ✈️ Đội ngũ Flight Management
                                </p>
                                <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                    Bảo mật • Tin cậy • Hiệu quả<br>
                                    Yêu cầu được gửi vào: %s UTC
                                </p>
                            </div>
                    
                        </div>
                    </body>
                    </html>
                    """, resetLink, resetLink, currentTime);

            // Đặt cả nội dung văn bản và HTML
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email đặt lại mật khẩu thất bại", e);
        }
    }

    public void sendBookingConfirmationEmail(EmailBookingRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(request.getEmail());
            helper.setSubject("✈️ Xác nhận đặt chỗ - " + request.getConfirmationCode());
            helper.setFrom(fromEmail);

            // Tạo cả nội dung văn bản thuần túy và HTML
            String plainTextContent = generateBookingPlainText(request);
            String htmlContent = generateBookingEmailHtml(request);

            // Đặt cả nội dung văn bản và HTML
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email xác nhận đặt chỗ thất bại", e);
        }
    }

    /**
     * Send single ticket booking confirmation
     */
    public void sendSingleTicketConfirmation(String to, String customerName, String passengerName,
                                             String confirmationCode, String flightCode, String departureCity,
                                             String arrivalCity, String departureTime, String seatNumber,
                                             BigDecimal fare, boolean needsPayment) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🎫 Xác nhận đặt vé - " + confirmationCode);

            String currentTime = "2025-06-11 07:34:18";
            String paymentStatus = needsPayment ? "⏱️ Chờ thanh toán" : "✅ Đã thanh toán";

            String paymentSection = needsPayment ?
                    String.format("""
                            
                            ⚠️ YÊU CẦU THANH TOÁN:
                            ======================
                            Vé này cần thanh toán để được xác nhận.
                            Vui lòng hoàn tất thanh toán tại: %s/payment/%s
                            
                            Lưu ý: Vé chưa thanh toán có thể bị hủy tự động.
                            """, frontendUrl, confirmationCode) : "";

            // Plain text version
            String plainTextContent = String.format("""
                            ✈️ FLIGHT MANAGEMENT - XÁC NHẬN ĐẶT VÉ
                            ========================================
                            
                            Kính chào %s!
                            
                            🎫 ĐẶT VÉ THÀNH CÔNG!
                            
                            Vé máy bay của bạn đã được đặt thành công. Dưới đây là thông tin chi tiết:
                            
                            📋 THÔNG TIN VÉ:
                            =================
                            Mã xác nhận: %s
                            Khách hàng: %s
                            Hành khách: %s
                            Chuyến bay: %s
                            Tuyến đường: %s → %s
                            Khởi hành: %s
                            Số ghế: %s
                            Giá vé: %s VND
                            Trạng thái: %s
                            %s
                            ✈️ CHUẨN BỊ CHO CHUYẾN BAY:
                            ============================
                            • Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành
                            • Mang theo giấy tờ tùy thân hợp lệ (CCCD/Hộ chiếu)
                            • In vé điện tử hoặc lưu mã xác nhận trên điện thoại
                            • Kiểm tra quy định hành lý của hãng bay
                            
                            📱 QUẢN LÝ ĐẶT CHỖ:
                            ====================
                            Truy cập: %s/booking-lookup
                            Nhập mã xác nhận: %s
                            
                            ⚠️ QUAN TRỌNG: 
                            Vui lòng lưu mã xác nhận này để tra cứu và quản lý đặt chỗ.
                            
                            📞 HỖ TRỢ KHÁCH HÀNG:
                            =====================
                            Email: support@thinhuit.id.vn
                            Hotline: 1900-1234 (24/7)
                            Website: %s
                            
                            Cảm ơn bạn đã chọn Flight Management!
                            
                            ==========================================
                            ✈️ Đội ngũ Flight Management
                            Bảo mật • Tin cậy • Hiệu quả
                            Email được gửi vào: %s UTC bởi %s
                            ==========================================
                            
                            Đây là email tự động. Vui lòng không trả lời email này.
                            """, customerName, confirmationCode, customerName, passengerName, flightCode,
                    departureCity, arrivalCity, departureTime, seatNumber, fare, paymentStatus,
                    paymentSection, frontendUrl, confirmationCode, frontendUrl, currentTime, "thinh0704hcm");

            // HTML version
            String htmlContent = generateSingleTicketHtml(customerName, passengerName, confirmationCode,
                    flightCode, departureCity, arrivalCity, departureTime, seatNumber, fare, needsPayment, currentTime);

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email xác nhận đặt vé thất bại", e);
        }
    }

    /**
     * Generate HTML content for single ticket confirmation
     */
    private String generateSingleTicketHtml(String customerName, String passengerName, String confirmationCode,
                                            String flightCode, String departureCity, String arrivalCity,
                                            String departureTime, String seatNumber, BigDecimal fare,
                                            boolean needsPayment, String currentTime) {

        String paymentButton = "";
        String paymentWarning = "";
        String statusColor = needsPayment ? "#ffc107" : "#28a745";
        String statusTextColor = needsPayment ? "black" : "white";
        String statusText = needsPayment ? "⏱️ Chờ thanh toán" : "✅ Đã thanh toán";

        if (needsPayment) {
            String paymentLink = frontendUrl + "/payment/" + confirmationCode;
            paymentButton = String.format("""
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="display: inline-block; padding: 15px 30px;
                                  background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%);
                                  color: #ffffff; text-decoration: none; border-radius: 8px;
                                  font-weight: bold; font-size: 16px;
                                  box-shadow: 0 4px 15px rgba(40, 167, 69, 0.4);">
                            💳 Hoàn tất thanh toán - %s VND
                        </a>
                    </div>
                    """, paymentLink, fare);

            paymentWarning = """
                    <div style="text-align: center; color: #dc3545; font-weight: bold;
                              margin: 20px 0; background-color: #f8d7da; padding: 15px;
                              border-radius: 6px; border: 1px solid #f5c6cb;">
                        <p style="margin: 0 0 10px 0;">
                            ⚠️ Yêu cầu thanh toán: Vé này cần thanh toán để được xác nhận.
                        </p>
                        <p style="margin: 0; font-size: 14px;">
                            Vé chưa thanh toán có thể bị hủy tự động.
                        </p>
                    </div>
                    """;
        }

        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Xác nhận đặt vé - %s</title>
                        </head>
                        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                            <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                        
                                <!-- Header -->
                                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                                    <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                        ✈️ Flight Management
                                    </h1>
                                    <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                        Xác nhận đặt vé
                                    </p>
                                    <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                                        Đặt vé: %s UTC bởi %s
                                    </p>
                                </div>
                        
                                <!-- Success Message -->
                                <div style="padding: 30px; text-align: center; background-color: #d4edda; border-bottom: 1px solid #c3e6cb;">
                                    <div style="font-size: 48px; margin-bottom: 15px;">🎫</div>
                                    <h2 style="color: #155724; margin: 0 0 15px 0;">Đặt vé thành công!</h2>
                                    <p style="color: #155724; margin: 0;">
                                        Kính chào %s, vé máy bay đã được đặt thành công
                                    </p>
                                    <div style="margin: 15px 0;">
                                        <span style="background-color: %s; color: %s; padding: 10px 20px; border-radius: 25px; font-weight: bold; font-size: 14px;">
                                            %s
                                        </span>
                                    </div>
                                </div>
                        
                                %s
                                %s
                        
                                <!-- Ticket Information -->
                                <div style="padding: 30px;">
                                    <h3 style="color: #007bff; margin-bottom: 20px; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                                        🎫 Thông tin vé máy bay
                                    </h3>
                        
                                    <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                                        <tr style="background-color: #f8f9fa;">
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold; width: 35%%;">Mã xác nhận:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-family: monospace; font-size: 16px; font-weight: bold; color: #007bff;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Khách hàng:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                        </tr>
                                        <tr style="background-color: #f8f9fa;">
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Hành khách:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Chuyến bay:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold; color: #007bff;">%s</td>
                                        </tr>
                                        <tr style="background-color: #f8f9fa;">
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Tuyến đường:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s → %s</td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Khởi hành:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                        </tr>
                                        <tr style="background-color: #f8f9fa;">
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Số ghế:</td>
                                            <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">
                                                <span style="background-color: #007bff; color: white; padding: 6px 12px; border-radius: 12px; font-weight: bold;">%s</span>
                                            </td>
                                        </tr>
                                        <tr style="background-color: #d4edda;">
                                            <td style="padding: 15px; font-weight: bold; font-size: 16px;">Giá vé:</td>
                                            <td style="padding: 15px; font-size: 18px; font-weight: bold; color: #28a745;">%s VND</td>
                                        </tr>
                                    </table>
                        
                                    <!-- Preparation Instructions -->
                                    <h4 style="color: #007bff; margin: 30px 0 15px 0;">✈️ Chuẩn bị cho chuyến bay</h4>
                                    <ul style="color: #666; line-height: 1.8; margin: 0; padding-left: 20px;">
                                        <li>Có mặt tại sân bay <strong>ít nhất 2 tiếng trước</strong> giờ khởi hành</li>
                                        <li>Mang theo <strong>giấy tờ tùy thân hợp lệ</strong> (CCCD/Hộ chiếu)</li>
                                        <li>In vé điện tử hoặc lưu mã xác nhận trên điện thoại</li>
                                        <li>Kiểm tra quy định hành lý của hãng bay</li>
                                    </ul>
                        
                                    <!-- Booking Management -->
                                    <div style="text-align: center; margin: 30px 0;">
                                        <a href="%s/booking-lookup"
                                           style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #007bff 0%%, #0056b3 100%%);
                                                  color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold;
                                                  font-size: 16px; box-shadow: 0 4px 15px rgba(0, 123, 255, 0.4);">
                                            📱 Quản lý đặt chỗ
                                        </a>
                                    </div>
                        
                                    <!-- Important Notice -->
                                    <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                        <h4 style="color: #856404; margin: 0 0 15px 0; font-size: 16px;">
                                            ⚠️ Quan trọng
                                        </h4>
                                        <p style="color: #856404; margin: 0; line-height: 1.6;">
                                            Vui lòng lưu mã xác nhận <strong>%s</strong> để tra cứu và quản lý đặt chỗ của bạn.
                                        </p>
                                    </div>
                        
                                    <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0; text-align: center;">
                                        Nếu bạn có thắc mắc, liên hệ hỗ trợ tại
                                        <a href="mailto:support@thinhuit.id.vn" style="color: #007bff;">support@thinhuit.id.vn</a>
                                        hoặc gọi hotline <strong>1900-1234</strong>
                                    </p>
                                </div>
                        
                                <!-- Footer -->
                                <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                    <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                        ✈️ Đội ngũ Flight Management
                                    </p>
                                    <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                        Bảo mật • Tin cậy • Hiệu quả<br>
                                        Cảm ơn bạn đã chọn chúng tôi!
                                    </p>
                                </div>
                        
                            </div>
                        </body>
                        </html>
                        """, confirmationCode, currentTime, "thinh0704hcm", customerName, statusColor, statusTextColor, statusText,
                paymentButton, paymentWarning, confirmationCode, customerName, passengerName, flightCode,
                departureCity, arrivalCity, departureTime, seatNumber, fare, frontendUrl, confirmationCode);
    }

    public void sendPassengerPaymentNotification(String to, String passengerName, String confirmationCode,
                                                 String flightCode, String departureCity, String arrivalCity,
                                                 String departureTime, String seatNumber, BigDecimal fare) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("✅ Thanh toán thành công - Vé máy bay " + confirmationCode);

            String currentTime = "2025-06-11 07:27:09";

            // Plain text version
            String plainTextContent = String.format("""
                            ✈️ FLIGHT MANAGEMENT - THANH TOÁN THÀNH CÔNG
                            =============================================
                            
                            Kính chào %s!
                            
                            ✅ THANH TOÁN THÀNH CÔNG!
                            
                            Chúng tôi xác nhận rằng thanh toán cho vé máy bay của bạn đã được xử lý thành công.
                            
                            📋 THÔNG TIN VÉ CỦA BẠN:
                            =========================
                            Mã xác nhận: %s
                            Tên hành khách: %s
                            Chuyến bay: %s
                            Tuyến đường: %s → %s
                            Khởi hành: %s
                            Số ghế: %s
                            Giá vé: %s VND
                            
                            📧 VÉ ĐIỆN TỬ:
                            ===============
                            Vé điện tử của bạn đã được gửi kèm trong email này.
                            Bạn có thể sử dụng vé điện tử này để làm thủ tục tại sân bay.
                            
                            ✈️ CHUẨN BỊ CHO CHUYẾN BAY:
                            ============================
                            • Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành
                            • Mang theo giấy tờ tùy thân hợp lệ (CCCD/Hộ chiếu)
                            • In vé điện tử hoặc lưu trên điện thoại
                            • Kiểm tra quy định hành lý của hãng bay
                            
                            📱 QUẢN LÝ ĐẶT CHỖ:
                            ====================
                            Truy cập: %s/booking-lookup
                            Nhập mã xác nhận: %s
                            
                            📞 HỖ TRỢ KHÁCH HÀNG:
                            =====================
                            Nếu bạn có thắc mắc về chuyến bay:
                            - Email: support@thinhuit.id.vn
                            - Hotline: 1900-1234 (24/7)
                            - Website: %s
                            
                            Cảm ơn bạn đã chọn Flight Management!
                            Chúc bạn có chuyến bay an toàn và thoải mái!
                            
                            ==========================================
                            ✈️ Đội ngũ Flight Management
                            Bảo mật • Tin cậy • Hiệu quả
                            Email được gửi vào: %s UTC bởi %s
                            ==========================================
                            
                            Đây là email tự động. Vui lòng không trả lời email này.
                            """, passengerName, confirmationCode, passengerName, flightCode, departureCity, arrivalCity,
                    departureTime, seatNumber, fare, frontendUrl, confirmationCode, frontendUrl, currentTime, "thinh0704hcm");

            // HTML version
            String htmlContent = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>Thanh toán thành công - %s</title>
                            </head>
                            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                            
                                    <!-- Header -->
                                    <div style="background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); padding: 30px; text-align: center;">
                                        <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                            ✈️ Flight Management
                                        </h1>
                                        <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                            Thanh toán thành công
                                        </p>
                                        <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                                            Xử lý: %s UTC bởi %s
                                        </p>
                                    </div>
                            
                                    <!-- Success Message -->
                                    <div style="padding: 30px; text-align: center; background-color: #d4edda; border-bottom: 1px solid #c3e6cb;">
                                        <div style="font-size: 48px; margin-bottom: 15px;">✅</div>
                                        <h2 style="color: #155724; margin: 0 0 15px 0;">Thanh toán thành công!</h2>
                                        <p style="color: #155724; margin: 0;">
                                            Kính chào %s, vé máy bay của bạn đã được xác nhận
                                        </p>
                                    </div>
                            
                                    <!-- Ticket Information -->
                                    <div style="padding: 30px;">
                                        <h3 style="color: #28a745; margin-bottom: 20px; font-size: 18px; border-bottom: 2px solid #28a745; padding-bottom: 10px;">
                                            🎫 Thông tin vé máy bay
                                        </h3>
                            
                                        <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                                            <tr style="background-color: #f8f9fa;">
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold; width: 35%%;">Mã xác nhận:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-family: monospace; font-size: 16px; font-weight: bold; color: #007bff;">%s</td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Tên hành khách:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                            </tr>
                                            <tr style="background-color: #f8f9fa;">
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Chuyến bay:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold; color: #007bff;">%s</td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Tuyến đường:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s → %s</td>
                                            </tr>
                                            <tr style="background-color: #f8f9fa;">
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Khởi hành:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                            </tr>
                                            <tr>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Số ghế:</td>
                                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">
                                                    <span style="background-color: #007bff; color: white; padding: 6px 12px; border-radius: 12px; font-weight: bold;">%s</span>
                                                </td>
                                            </tr>
                                            <tr style="background-color: #d4edda;">
                                                <td style="padding: 15px; font-weight: bold; font-size: 16px;">Giá vé:</td>
                                                <td style="padding: 15px; font-size: 18px; font-weight: bold; color: #28a745;">%s VND</td>
                                            </tr>
                                        </table>
                            
                                        <!-- Electronic Ticket Section -->
                                        <div style="background-color: #e7f3ff; border: 1px solid #bee5eb; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                            <h4 style="color: #0c5460; margin: 0 0 15px 0; font-size: 16px;">
                                                📧 Vé điện tử của bạn
                                            </h4>
                                            <p style="color: #0c5460; margin: 0; line-height: 1.6;">
                                                Vé điện tử đã được gửi kèm trong email này. Bạn có thể in ra hoặc lưu trên điện thoại để sử dụng tại sân bay.
                                            </p>
                                        </div>
                            
                                        <!-- Preparation Instructions -->
                                        <h4 style="color: #28a745; margin: 30px 0 15px 0;">✈️ Chuẩn bị cho chuyến bay</h4>
                                        <ul style="color: #666; line-height: 1.8; margin: 0; padding-left: 20px;">
                                            <li>Có mặt tại sân bay <strong>ít nhất 2 tiếng trước</strong> giờ khởi hành</li>
                                            <li>Mang theo <strong>giấy tờ tùy thân hợp lệ</strong> (CCCD/Hộ chiếu)</li>
                                            <li>In vé điện tử hoặc lưu trên điện thoại</li>
                                            <li>Kiểm tra quy định hành lý của hãng bay</li>
                                        </ul>
                            
                                        <!-- Booking Management -->
                                        <div style="text-align: center; margin: 30px 0;">
                                            <a href="%s/booking-lookup"
                                               style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #007bff 0%%, #0056b3 100%%);
                                                      color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold;
                                                      font-size: 16px; box-shadow: 0 4px 15px rgba(0, 123, 255, 0.4);">
                                                📱 Quản lý đặt chỗ
                                            </a>
                                        </div>
                            
                                        <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0; text-align: center;">
                                            Nếu bạn có thắc mắc, liên hệ hỗ trợ tại
                                            <a href="mailto:support@thinhuit.id.vn" style="color: #28a745;">support@thinhuit.id.vn</a>
                                            hoặc gọi hotline <strong>1900-1234</strong>
                                        </p>
                                    </div>
                            
                                    <!-- Footer -->
                                    <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                            ✈️ Đội ngũ Flight Management
                                        </p>
                                        <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                            Bảo mật • Tin cậy • Hiệu quả<br>
                                            Cảm ơn bạn đã chọn chúng tôi!
                                        </p>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """, confirmationCode, currentTime, "thinh0704hcm", passengerName, confirmationCode, passengerName,
                    flightCode, departureCity, arrivalCity, departureTime, seatNumber, fare, frontendUrl);

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email thông báo thanh toán cho hành khách thất bại", e);
        }
    }

    private String generateBookingPlainText(EmailBookingRequest request) {
        try {
            // Phân tích dữ liệu đặt chỗ với tính an toàn kiểu dữ liệu
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> bookingData = objectMapper.convertValue(request.getBookingData(), typeRef);

            // Trích xuất dữ liệu an toàn
            String confirmationCode = request.getConfirmationCode();
            Map<String, Object> flightInfo = getMapValue(bookingData, "flightInfo");
            List<Map<String, Object>> tickets = getListValue(bookingData, "tickets");
            List<String> passengers = getListValue(bookingData, "passengers");
            Object totalAmount = bookingData.get("totalAmount");
            String bookingDate = (String) bookingData.get("bookingDate");

            // Dấu thời gian hiện tại: 2025-06-11 05:27:05
            String formattedDateTime = "2025-06-11 05:27:05";

            // Kiểm tra xem có cần thanh toán không
            boolean isPaid = tickets != null && tickets.stream().allMatch(ticket -> {
                Object status = ticket.get("ticketStatus");
                return status != null && ((Number) status).intValue() == 1;
            });

            // Xây dựng danh sách hành khách
            StringBuilder passengerList = new StringBuilder();
            if (tickets != null) {
                for (int i = 0; i < tickets.size(); i++) {
                    Map<String, Object> ticket = tickets.get(i);
                    String passengerName = passengers != null && i < passengers.size() ? passengers.get(i) : "Hành khách " + (i + 1);
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

            // Phần thanh toán - Cập nhật để sử dụng liên kết thanh toán frontend
            String paymentSection = "";
            if (request.isIncludePaymentButton() && !isPaid) {
                String paymentLink = generatePaymentLink(confirmationCode);
                paymentSection = String.format("""
                        
                        ⚠️ YÊU CẦU THANH TOÁN
                        ======================
                        Đặt chỗ này cần thanh toán để được xác nhận.
                        Những đặt chỗ chưa thanh toán có thể bị hủy tự động 24 giờ trước khởi hành.
                        
                        Để hoàn tất thanh toán, vui lòng truy cập:
                        %s
                        
                        Hoặc sao chép và dán liên kết trên vào trình duyệt của bạn.
                        
                        Liên kết này sẽ đưa bạn đến trang thanh toán an toàn để hoàn tất giao dịch.
                        
                        """, paymentLink);
            }

            // Xây dựng email văn bản thuần túy hoàn chỉnh
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
                            """, isPaid ? "✓ Đã thanh toán" : "⏱️ Chờ thanh toán", // trạng thái
                    formattedDateTime, "thinh0704hcm", // thông tin tiêu đề
                    confirmationCode, // mã xác nhận
                    paymentSection, // phần thanh toán
                    safeGet(flightInfo, "flightCode", "N/A"), // mã chuyến bay
                    safeGet(flightInfo, "departureCity", "N/A"), safeGet(flightInfo, "arrivalCity", "N/A"), // tuyến đường
                    formatDateTime(safeGet(flightInfo, "departureTime", "")), // khởi hành
                    flightInfo != null && flightInfo.get("arrivalTime") != null ? String.format("Đến: %s", formatDateTime((String) flightInfo.get("arrivalTime"))) : "", // đến nếu có
                    passengerList.toString(), // danh sách hành khách
                    formatDate(bookingDate), // ngày đặt
                    tickets != null ? tickets.size() : 0, // số hành khách
                    isPaid ? "Đã thanh toán" : "Chờ thanh toán", // trạng thái thanh toán
                    totalAmount, // tổng tiền
                    confirmationCode, // mã xác nhận cho bước tiếp theo
                    formattedDateTime // dấu thời gian chân trang
            );

        } catch (Exception e) {
            // Văn bản thuần túy dự phòng nếu có lỗi
            return String.format("""
                    ✈️ FLIGHT MANAGEMENT - XÁC NHẬN ĐẶT CHỖ
                    ==========================================
                    
                    🎉 ĐẶT CHỖ THÀNH CÔNG!
                    
                    Mã xác nhận: %s
                    In ngày: 2025-06-11 05:27:05 UTC | Bởi: thinh0704hcm
                    
                    ⚠️ QUAN TRỌNG: Vui lòng lưu mã xác nhận này.
                    Bạn sẽ cần nó để truy xuất hoặc quản lý đặt chỗ sau này.
                    
                    Để xem chi tiết đầy đủ và thanh toán, vui lòng truy cập:
                    %s
                    
                    📞 HỖ TRỢ KHÁCH HÀNG: support@thinhuit.id.vn
                    
                    ==========================================
                    ✈️ Đội ngũ Flight Management
                    Email được gửi vào: 2025-06-11 05:27:05 UTC
                    ==========================================
                    """, request.getConfirmationCode(), generatePaymentLink(request.getConfirmationCode()));
        }
    }

    private String generateBookingEmailHtml(EmailBookingRequest request) {
        try {
            // Phân tích dữ liệu đặt chỗ với tính an toàn kiểu dữ liệu
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> bookingData = objectMapper.convertValue(request.getBookingData(), typeRef);

            // Trích xuất dữ liệu an toàn với tính an toàn kiểu dữ liệu
            String confirmationCode = request.getConfirmationCode();
            Map<String, Object> flightInfo = getMapValue(bookingData, "flightInfo");
            List<Map<String, Object>> tickets = getListValue(bookingData, "tickets");
            List<String> passengers = getListValue(bookingData, "passengers");
            Object totalAmount = bookingData.get("totalAmount");
            String bookingDate = (String) bookingData.get("bookingDate");

            // Định dạng dấu thời gian - Cập nhật thời gian hiện tại: 2025-06-11 05:27:05
            String formattedDateTime = "2025-06-11 05:27:05";

            // Kiểm tra xem có cần thanh toán không
            boolean isPaid = tickets != null && tickets.stream().allMatch(ticket -> {
                Object status = ticket.get("ticketStatus");
                return status != null && ((Number) status).intValue() == 1;
            });

            // Tạo nút thanh toán nếu cần - Cập nhật để sử dụng liên kết thanh toán frontend
            String paymentButton = "";
            String paymentWarning = "";

            if (request.isIncludePaymentButton() && !isPaid) {
                String paymentLink = generatePaymentLink(confirmationCode);
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
                        <p style="text-align: center; color: #666; font-size: 14px; margin: 10px 0;">
                            Hoặc sao chép liên kết này vào trình duyệt:<br>
                            <span style="word-break: break-all; color: #007bff; font-family: monospace;">%s</span>
                        </p>
                        """, paymentLink, totalAmount, paymentLink);

                paymentWarning = """
                        <div style="text-align: center; color: #dc3545; font-weight: bold;
                                  margin: 20px 0; background-color: #f8d7da; padding: 15px;
                                  border-radius: 6px; border: 1px solid #f5c6cb;">
                            <p style="margin: 0 0 10px 0;">
                                ⚠️ Yêu cầu thanh toán: Đặt chỗ này cần thanh toán để được xác nhận.
                            </p>
                            <p style="margin: 0; font-size: 14px;">
                                Những đặt chỗ chưa thanh toán có thể bị hủy tự động 24 giờ trước khởi hành.
                            </p>
                        </div>
                        """;
            }

            // Tạo danh sách hành khách
            StringBuilder passengerRows = new StringBuilder();
            if (tickets != null) {
                for (int i = 0; i < tickets.size(); i++) {
                    Map<String, Object> ticket = tickets.get(i);
                    String passengerName = passengers != null && i < passengers.size() ? passengers.get(i) : "Hành khách " + (i + 1);
                    Object fare = ticket.get("fare");
                    Object seatNumber = ticket.get("seatNumber");
                    Object statusObj = ticket.get("ticketStatus");
                    int ticketStatus = statusObj != null ? ((Number) statusObj).intValue() : 0;

                    String statusBadge = ticketStatus == 1 ? "<span style='background-color: #28a745; color: white; padding: 4px 8px; border-radius: 12px; font-size: 12px;'>Đã thanh toán</span>" : "<span style='background-color: #ffc107; color: black; padding: 4px 8px; border-radius: 12px; font-size: 12px;'>Chờ thanh toán</span>";

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

            // Xây dựng HTML hoàn chỉnh với kiểu tiêu đề/chân trang đặt lại mật khẩu
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
                            
                                    <!-- Tiêu đề (Kiểu đặt lại mật khẩu) -->
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
                            
                                    <!-- Thông báo thành công -->
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
                            
                                    <!-- Mã xác nhận -->
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
                            
                                    <!-- Nội dung chính -->
                                    <div style="padding: 30px;">
                                        <!-- Thông tin chuyến bay -->
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
                            
                                        <!-- Thông tin hành khách -->
                                        <h3 style="color: #007bff; margin: 30px 0 20px 0; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                                            Thông tin hành khách
                                        </h3>
                                        <table style="width: 100%%; border-collapse: collapse; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                                            %s
                                        </table>
                            
                                        <!-- Tóm tắt đặt chỗ -->
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
                            
                                        <!-- Bước tiếp theo -->
                                        <h4 style="color: #333; margin: 30px 0 15px 0;">Tiếp theo là gì?</h4>
                                        <ul style="color: #666; line-height: 1.6; margin: 0; padding-left: 20px;">
                                            <li>Lưu mã xác nhận của bạn: <strong style="color: #007bff;">%s</strong></li>
                                            <li>Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành</li>
                                            <li>Mang theo giấy tờ tùy thân hợp lệ và mã xác nhận</li>
                                            <li>Bạn có thể quản lý đặt chỗ trực tuyến bằng mã xác nhận</li>
                                            %s
                                        </ul>
                                        <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                            Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, vui lòng liên hệ đội hỗ trợ của chúng tôi tại
                                            <a href="mailto:support@thinhuit.id.vn" style="color: #667eea;">support@thinhuit.id.vn</a>
                                        </p>
                                    </div>
                            
                                    <!-- Chân trang (Kiểu đặt lại mật khẩu) -->
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
                            """, confirmationCode, // tiêu đề
                    formattedDateTime, "thinh0704hcm", // thông tin tiêu đề với người dùng hiện tại
                    isPaid ? "#28a745" : "#ffc107", // màu huy hiệu trạng thái
                    isPaid ? "white" : "black", // màu văn bản huy hiệu trạng thái
                    isPaid ? "✓ Đã thanh toán" : "⏱️ Chờ thanh toán", // văn bản trạng thái
                    paymentButton, // nút thanh toán
                    paymentWarning, // cảnh báo thanh toán
                    confirmationCode, // mã xác nhận
                    safeGet(flightInfo, "flightCode", "N/A"), // mã chuyến bay
                    safeGet(flightInfo, "departureCity", "N/A"), safeGet(flightInfo, "arrivalCity", "N/A"), // tuyến đường
                    formatDateTime(safeGet(flightInfo, "departureTime", "")), // khởi hành
                    flightInfo != null && flightInfo.get("arrivalTime") != null ? String.format("<tr><td style='padding: 12px; border-bottom: 1px dotted #ccc;'><strong>Đến:</strong></td><td style='padding: 12px; border-bottom: 1px dotted #ccc;'>%s</td></tr>", formatDateTime((String) flightInfo.get("arrivalTime"))) : "", // đến nếu có
                    passengerRows.toString(), // hàng hành khách
                    formatDate(bookingDate), // ngày đặt
                    tickets != null ? tickets.size() : 0, // số hành khách
                    isPaid ? "Đã thanh toán" : "Chờ thanh toán", // trạng thái thanh toán
                    totalAmount, // tổng tiền
                    confirmationCode, // mã xác nhận bước tiếp theo
                    !isPaid ? String.format("<li>Hoàn tất thanh toán tại: <a href='%s' style='color: #007bff;'>%s</a></li>", generatePaymentLink(confirmationCode), generatePaymentLink(confirmationCode)) : "", // liên kết thanh toán trong bước tiếp theo
                    formattedDateTime // dấu thời gian chân trang
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HTML email: " + e.getMessage(), e);
        }
    }

    /**
     * Send welcome email to new customer
     */
    public void sendCustomerWelcomeEmail(String to, String customerName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🎉 Chào mừng bạn đến với Flight Management!");

            String currentTime = "2025-06-11 07:10:44";

            // Plain text version
            String plainTextContent = String.format("""
                    ✈️ FLIGHT MANAGEMENT - CHÀO MỪNG!
                    =====================================
                    
                    Xin chào %s!
                    
                    🎉 Chào mừng bạn đến với Flight Management!
                    
                    Tài khoản của bạn đã được tạo thành công. Bây giờ bạn có thể:
                    
                    ✅ Đặt vé máy bay dễ dàng
                    ✅ Quản lý các chuyến bay của mình
                    ✅ Theo dõi lịch sử đặt vé
                    ✅ Nhận thông báo về trạng thái chuyến bay
                    ✅ Hỗ trợ khách hàng 24/7
                    
                    🚀 BẮT ĐẦU NGAY:
                    - Truy cập trang web: %s
                    - Đăng nhập bằng email và mật khẩu của bạn
                    - Khám phá các ưu đãi đặc biệt dành cho thành viên mới
                    
                    💡 MẸO HỮU ÍCH:
                    - Lưu mã xác nhận sau mỗi lần đặt vé
                    - Đăng ký nhận thông báo để không bỏ lỡ ưu đãi
                    - Liên hệ hỗ trợ nếu cần giúp đỡ
                    
                    📞 HỖ TRỢ KHÁCH HÀNG:
                    Email: support@thinhuit.id.vn
                    Hotline: 1900-1234 (24/7)
                    
                    Cảm ơn bạn đã chọn Flight Management cho các chuyến đi của mình!
                    
                    ==========================================
                    ✈️ Đội ngũ Flight Management
                    Bảo mật • Tin cậy • Hiệu quả
                    Email được gửi vào: %s UTC bởi %s
                    ==========================================
                    
                    Đây là email tự động. Vui lòng không trả lời email này.
                    """, customerName, frontendUrl, currentTime, "thinh0704hcm");

            // HTML version
            String htmlContent = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Chào mừng bạn đến với Flight Management!</title>
                    </head>
                    <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                    
                            <!-- Header -->
                            <div style="background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); padding: 30px; text-align: center;">
                                <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                    ✈️ Flight Management
                                </h1>
                                <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                    Chào mừng thành viên mới!
                                </p>
                                <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                                    Tạo tài khoản: %s UTC bởi %s
                                </p>
                            </div>
                    
                            <!-- Welcome Message -->
                            <div style="padding: 30px; text-align: center; background-color: #d4edda; border-bottom: 1px solid #c3e6cb;">
                                <div style="font-size: 48px; margin-bottom: 15px;">🎉</div>
                                <h2 style="color: #155724; margin: 0 0 15px 0;">Chào mừng %s!</h2>
                                <p style="color: #155724; margin: 0;">
                                    Tài khoản của bạn đã được tạo thành công
                                </p>
                            </div>
                    
                            <!-- Main Content -->
                            <div style="padding: 30px;">
                                <h3 style="color: #28a745; margin-bottom: 20px; font-size: 18px;">
                                    🚀 Bắt đầu hành trình với chúng tôi
                                </h3>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 0 0 25px 0;">
                                    Bây giờ bạn có thể tận hưởng tất cả các dịch vụ tuyệt vời của Flight Management:
                                </p>
                    
                                <!-- Features List -->
                                <div style="margin: 20px 0;">
                                    <div style="padding: 15px; margin: 10px 0; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #28a745;">
                                        <strong style="color: #28a745;">✅ Đặt vé máy bay dễ dàng</strong><br>
                                        <small style="color: #666;">Tìm kiếm và đặt vé chỉ trong vài click</small>
                                    </div>
                                    <div style="padding: 15px; margin: 10px 0; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #17a2b8;">
                                        <strong style="color: #17a2b8;">✅ Quản lý chuyến bay</strong><br>
                                        <small style="color: #666;">Theo dõi và quản lý tất cả chuyến bay của bạn</small>
                                    </div>
                                    <div style="padding: 15px; margin: 10px 0; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #ffc107;">
                                        <strong style="color: #856404;">✅ Lịch sử đặt vé</strong><br>
                                        <small style="color: #666;">Xem lại tất cả các chuyến đi đã đặt</small>
                                    </div>
                                    <div style="padding: 15px; margin: 10px 0; background-color: #f8f9fa; border-radius: 8px; border-left: 4px solid #dc3545;">
                                        <strong style="color: #dc3545;">✅ Hỗ trợ 24/7</strong><br>
                                        <small style="color: #666;">Đội ngũ hỗ trợ luôn sẵn sàng giúp đỡ bạn</small>
                                    </div>
                                </div>
                    
                                <!-- CTA Button -->
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s"
                                       style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%);
                                              color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold;
                                              font-size: 16px; box-shadow: 0 4px 15px rgba(40, 167, 69, 0.4);">
                                        🚀 Bắt đầu đặt vé ngay
                                    </a>
                                </div>
                    
                                <!-- Tips Section -->
                                <div style="background-color: #e7f3ff; border: 1px solid #bee5eb; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                    <h4 style="color: #0c5460; margin: 0 0 15px 0; font-size: 16px;">
                                        💡 Mẹo hữu ích cho thành viên mới
                                    </h4>
                                    <ul style="color: #0c5460; margin: 0; padding-left: 20px; line-height: 1.6;">
                                        <li>Lưu mã xác nhận sau mỗi lần đặt vé để dễ dàng tra cứu</li>
                                        <li>Đăng ký nhận thông báo để không bỏ lỡ các ưu đãi đặc biệt</li>
                                        <li>Kiểm tra email thường xuyên để nhận thông tin cập nhật</li>
                                        <li>Liên hệ hỗ trợ nếu bạn cần bất kỳ sự giúp đỡ nào</li>
                                    </ul>
                                </div>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                    Nếu bạn có bất kỳ câu hỏi nào, đừng ngần ngại liên hệ với chúng tôi tại
                                    <a href="mailto:support@thinhuit.id.vn" style="color: #28a745;">support@thinhuit.id.vn</a>
                                    hoặc gọi hotline <strong>1900-1234</strong>
                                </p>
                            </div>
                    
                            <!-- Footer -->
                            <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                    ✈️ Đội ngũ Flight Management
                                </p>
                                <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                    Bảo mật • Tin cậy • Hiệu quả<br>
                                    Email được gửi vào: %s UTC bởi %s
                                </p>
                            </div>
                    
                        </div>
                    </body>
                    </html>
                    """, currentTime, "thinh0704hcm", customerName, frontendUrl, currentTime, "thinh0704hcm");

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email chào mừng khách hàng thất bại", e);
        }
    }

    /**
     * Send employee credentials email with temporary password
     */
    public void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName, String employeeTypeName,
                                             String tempPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔑 Flight Management - Thông tin tài khoản nhân viên");

            String currentTime = "2025-06-11 07:10:44";

            // Plain text version
            String plainTextContent = String.format("""
                    ✈️ FLIGHT MANAGEMENT - THÔNG TIN TÀI KHOẢN NHÂN VIÊN
                    =====================================================
                    
                    Xin chào %s!
                    
                    🎉 Chào mừng bạn gia nhập đội ngũ Flight Management!
                    
                    Tài khoản nhân viên của bạn đã được tạo thành công với thông tin sau:
                    
                    📋 THÔNG TIN TÀI KHOẢN:
                    ========================
                    Tên đăng nhập: %s
                    Email: %s
                    Mật khẩu tạm thời: %s
                    Chức vụ: %s
                    
                    🔐 HƯỚNG DẪN ĐĂNG NHẬP:
                    =======================
                    1. Truy cập: %s/login
                    2. Sử dụng tên đăng nhập và mật khẩu tạm thời ở trên
                    3. Hệ thống sẽ yêu cầu bạn đổi mật khẩu mới trong lần đăng nhập đầu tiên
                    4. Chọn mật khẩu mạnh và bảo mật
                    
                    ⚠️ QUAN TRỌNG:
                    - Đây là mật khẩu tạm thời, bạn PHẢI đổi mật khẩu ngay lần đầu đăng nhập
                    - Không chia sẻ thông tin đăng nhập với bất kỳ ai
                    - Luôn đăng xuất sau khi sử dụng xong
                    - Liên hệ IT nếu quên mật khẩu
                    
                    🚀 QUYỀN TRUY CẬP CỦA BẠN:
                    ==========================
                    Với vai trò "%s", bạn có thể:
                    • Truy cập hệ thống quản lý nội bộ
                    • Thực hiện các tác vụ theo phân quyền
                    • Xem báo cáo và thống kê
                    • Hỗ trợ khách hàng (nếu được phân quyền)
                    
                    📞 HỖ TRỢ KỸ THUẬT:
                    ===================
                    Nếu bạn gặp vấn đề khi đăng nhập hoặc sử dụng hệ thống:
                    - Email: it-support@thinhuit.id.vn
                    - Hotline nội bộ: 1900-1235
                    - Liên hệ quản lý trực tiếp
                    
                    ==========================================
                    ✈️ Đội ngũ Flight Management
                    Bảo mật • Tin cậy • Hiệu quả
                    Email được gửi vào: %s UTC bởi %s
                    ==========================================
                    
                    Đây là email tự động. Vui lòng không trả lời email này.
                    """, employeeName, accountName, to, tempPassword, employeeTypeName, frontendUrl, employeeTypeName, currentTime, "thinh0704hcm");

            // HTML version
            String htmlContent = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Thông tin tài khoản nhân viên</title>
                    </head>
                    <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff;">
                    
                            <!-- Header -->
                            <div style="background: linear-gradient(135deg, #6f42c1 0%%, #e83e8c 100%%); padding: 30px; text-align: center;">
                                <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                                    ✈️ Flight Management
                                </h1>
                                <p style="color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;">
                                    Thông tin tài khoản nhân viên
                                </p>
                                <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                                    Tạo tài khoản: %s UTC bởi %s
                                </p>
                            </div>
                    
                            <!-- Welcome Message -->
                            <div style="padding: 30px; text-align: center; background-color: #e7e3ff; border-bottom: 1px solid #d1c4e9;">
                                <div style="font-size: 48px; margin-bottom: 15px;">🔑</div>
                                <h2 style="color: #6f42c1; margin: 0 0 15px 0;">Chào mừng %s!</h2>
                                <p style="color: #6f42c1; margin: 0;">
                                    Tài khoản nhân viên của bạn đã sẵn sàng
                                </p>
                            </div>
                    
                            <!-- Credentials Section -->
                            <div style="padding: 30px;">
                                <h3 style="color: #6f42c1; margin-bottom: 20px; font-size: 18px; border-bottom: 2px solid #6f42c1; padding-bottom: 10px;">
                                    📋 Thông tin đăng nhập
                                </h3>
                    
                                <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                                    <tr style="background-color: #f8f9fa;">
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold; width: 30%%;">Tên đăng nhập:</td>
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-family: monospace; background-color: #e9ecef;">%s</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Email:</td>
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6;">%s</td>
                                    </tr>
                                    <tr style="background-color: #fff3cd;">
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Mật khẩu tạm thời:</td>
                                        <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-family: monospace; font-size: 18px; font-weight: bold; color: #856404;">%s</td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 15px; font-weight: bold;">Chức vụ:</td>
                                        <td style="padding: 15px;">
                                            <span style="background-color: #6f42c1; color: white; padding: 6px 12px; border-radius: 12px; font-size: 14px;">%s</span>
                                        </td>
                                    </tr>
                                </table>
                    
                                <!-- Login Instructions -->
                                <h4 style="color: #6f42c1; margin: 30px 0 15px 0;">🔐 Hướng dẫn đăng nhập</h4>
                                <ol style="color: #666; line-height: 1.8; margin: 0; padding-left: 20px;">
                                    <li>Truy cập trang đăng nhập: <a href="%s/login" style="color: #6f42c1; font-weight: bold;">%s/login</a></li>
                                    <li>Nhập tên đăng nhập và mật khẩu tạm thời ở trên</li>
                                    <li>Hệ thống sẽ yêu cầu bạn tạo mật khẩu mới</li>
                                    <li>Chọn mật khẩu mạnh và bảo mật</li>
                                </ol>
                    
                                <!-- Security Notice -->
                                <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 6px; padding: 20px; margin: 30px 0;">
                                    <h4 style="color: #721c24; margin: 0 0 15px 0; font-size: 16px;">
                                        ⚠️ Lưu ý bảo mật quan trọng
                                    </h4>
                                    <ul style="color: #721c24; margin: 0; padding-left: 20px; line-height: 1.6;">
                                        <li><strong>PHẢI đổi mật khẩu</strong> ngay lần đầu đăng nhập</li>
                                        <li>Không chia sẻ thông tin đăng nhập với bất kỳ ai</li>
                                        <li>Luôn đăng xuất sau khi sử dụng xong</li>
                                        <li>Liên hệ IT nếu nghi ngờ tài khoản bị xâm phạm</li>
                                    </ul>
                                </div>
                    
                                <!-- Permissions Section -->
                                <h4 style="color: #6f42c1; margin: 30px 0 15px 0;">🚀 Quyền truy cập của bạn</h4>
                                <div style="background-color: #e7e3ff; border-radius: 8px; padding: 20px; margin-bottom: 20px;">
                                    <p style="margin: 0 0 10px 0; font-weight: bold; color: #6f42c1;">Với vai trò "%s", bạn có thể:</p>
                                    <ul style="color: #6f42c1; margin: 0; padding-left: 20px; line-height: 1.6;">
                                        <li>Truy cập hệ thống quản lý nội bộ</li>
                                        <li>Thực hiện các tác vụ theo phân quyền</li>
                                        <li>Xem báo cáo và thống kê</li>
                                        <li>Hỗ trợ khách hàng (nếu được phân quyền)</li>
                                    </ul>
                                </div>
                    
                                <p style="color: #666666; line-height: 1.6; margin: 25px 0 0 0;">
                                    Nếu bạn gặp vấn đề kỹ thuật, vui lòng liên hệ IT Support tại
                                    <a href="mailto:it-support@thinhuit.id.vn" style="color: #6f42c1;">it-support@thinhuit.id.vn</a>
                                    hoặc hotline nội bộ <strong>1900-1235</strong>
                                </p>
                            </div>
                    
                            <!-- Footer -->
                            <div style="background-color: #f8f9fa; padding: 25px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                <p style="color: #6c757d; margin: 0 0 10px 0; font-weight: bold;">
                                    ✈️ Đội ngũ Flight Management
                                </p>
                                <p style="color: #6c757d; margin: 0; font-size: 12px;">
                                    Bảo mật • Tin cậy • Hiệu quả<br>
                                    Email được gửi vào: %s UTC bởi %s
                                </p>
                            </div>
                    
                        </div>
                    </body>
                    </html>
                    """, currentTime, "thinh0704hcm", employeeName, accountName, to, tempPassword, employeeTypeName, frontendUrl, frontendUrl, employeeTypeName, currentTime, "thinh0704hcm");

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email thông tin nhân viên thất bại", e);
        }
    }


    /**
     * Tạo liên kết thanh toán cho trang thanh toán frontend
     * Định dạng: {frontendUrl}/payment/{confirmationCode}
     */
    private String generatePaymentLink(String confirmationCode) {
        // Đảm bảo frontendUrl kết thúc bằng dấu gạch chéo
        String baseUrl = frontendUrl.endsWith("/") ? frontendUrl : frontendUrl + "/";
        return baseUrl + "payment/" + confirmationCode;
    }

    // Phương thức trợ giúp để trích xuất dữ liệu an toàn với kiểu dữ liệu phù hợp
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
            // Mẫu đã sửa - loại bỏ ký tự 'l' không hợp lệ, sử dụng 'H' cho giờ
            return java.time.LocalDateTime.parse(dateTimeString.replace("Z", "")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy lúc HH:mm"));
        } catch (Exception e) {
            return dateTimeString; // trở về chuỗi gốc
        }
    }

    private String formatDate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) return "N/A";
            return java.time.LocalDateTime.parse(dateString.replace("Z", "")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return dateString; // trở về chuỗi gốc
        }
    }
}