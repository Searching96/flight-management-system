package com.flightmanagement.service.impl;

import com.flightmanagement.dto.TicketDto;
import com.flightmanagement.entity.Flight;
import com.flightmanagement.entity.Passenger;
import com.flightmanagement.repository.FlightRepository;
import com.flightmanagement.repository.PassengerRepository;
import com.flightmanagement.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final PassengerRepository passengerRepository;

    private final FlightRepository flightRepository;

    public EmailServiceImpl(JavaMailSender mailSender, PassengerRepository passengerRepository, FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final DateTimeFormatter EMAIL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔐 Flight Management - Đặt lại mật khẩu");

            String resetLink = frontendUrl + "/reset-password/" + resetToken;
            String currentTime = Instant.now().atZone(ZoneOffset.UTC).format(EMAIL_DATETIME_FORMAT);

            // Phiên bản văn bản thuần túy
            String plainTextContent = String.format(
                    """
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
                            """,
                    resetLink, currentTime);

            // Phiên bản HTML
            String htmlContent = String.format(
                    """
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
                            """,
                    resetLink, resetLink, currentTime);

            // Đặt cả nội dung văn bản và HTML
            helper.setText(plainTextContent, htmlContent);
            System.out.println("Password reset email sent to " + mimeMessage);
            mailSender.send(mimeMessage);


        } catch (Exception e) {
            throw new RuntimeException("Gửi email đặt lại mật khẩu thất bại" + e.getMessage(), e);
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

            String currentTime = Instant.now().atZone(ZoneOffset.UTC).format(EMAIL_DATETIME_FORMAT);

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
                    Email được gửi vào: %s UTC
                    ==========================================
                    
                    Đây là email tự động. Vui lòng không trả lời email này.
                    """, customerName, frontendUrl, currentTime);

            // HTML version
            String htmlContent = String.format(
                    """
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
                                            Tạo tài khoản: %s UTC
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
                                            Email được gửi vào: %s UTC
                                        </p>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """,
                    currentTime, customerName, frontendUrl, currentTime);

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email chào mừng khách hàng thất bại", e);
        }
    }

    /**
     * Send employee credentials email with temporary password
     */
    public void sendEmployeeCredentialsEmail(String to, String employeeName, String accountName,
                                             String employeeTypeName,
                                             String tempPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔑 Flight Management - Thông tin tài khoản nhân viên");

            String currentTime = Instant.now().atZone(ZoneOffset.UTC).format(EMAIL_DATETIME_FORMAT);

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
                            Email được gửi vào: %s UTC
                            ==========================================
                            
                            Đây là email tự động. Vui lòng không trả lời email này.
                            """, employeeName, accountName, to, tempPassword, employeeTypeName, frontendUrl, employeeTypeName,
                    currentTime);

            // HTML version
            String htmlContent = String.format(
                    """
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
                                            Tạo tài khoản: %s UTC
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
                                            Email được gửi vào: %s UTC
                                        </p>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """,
                    currentTime, employeeName, accountName, to, tempPassword, employeeTypeName, frontendUrl,
                    frontendUrl, employeeTypeName, currentTime);

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email thông tin nhân viên thất bại", e);
        }
    }

    /**
     * Send booking confirmation email for multiple passengers
     */
    @Override
    public void sendBookingConfirmation(TicketDto ticketDto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);

            Passenger passenger = passengerRepository.findById(ticketDto.getPassengerId())
                    .orElseThrow(() -> new RuntimeException("Passenger not found with ID: " + ticketDto.getPassengerId()));

            Flight flight = flightRepository.findById(ticketDto.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight not found with code: " + ticketDto.getFlightId()));

            String to = passenger.getEmail();
            helper.setTo(to);
            helper.setSubject("🎫 Xác nhận đặt vé cho hành khách " + passenger.getPassengerName());

            String currentTime = Instant.now().atZone(ZoneOffset.UTC).format(EMAIL_DATETIME_FORMAT);
            String paymentStatus = ticketDto.getOrderId() == null ? "⏱️ Chờ thanh toán" : "✅ Đã thanh toán";

            // TEXT body
            String plainTextContent = String.format("""
                            ✈️ FLIGHT MANAGEMENT - XÁC NHẬN ĐẶT VÉ
                            
                            Chào %s,
                            
                            🎫 Vé của bạn đã được đặt thành công!
                            
                            -----------------------
                            THÔNG TIN HÀNH KHÁCH:
                            -----------------------
                            Tên: %s
                            Số ghế: %s
                            Giá vé: %s VND
                            
                            Trạng thái: %s
                            Ngày đặt: %s UTC
                            
                            Cảm ơn bạn đã sử dụng Flight Management!
                            """,
                    passenger.getPassengerName(),
                    passenger.getPassengerName(),
                    ticketDto.getSeatNumber(),
                    ticketDto.getFare(),
                    paymentStatus,
                    currentTime);

            // HTML body
            String htmlContent = generateSinglePassengerHtml(
                    passenger.getPassengerName(),
                    ticketDto.getConfirmationCode(),   // hoặc ticketDto.getOrderId() tuỳ bạn lưu
                    flight.getFlightCode(),
                    flight.getDepartureAirport().getCityName(),
                    flight.getArrivalAirport().getCityName(),
                    flight.getDepartureTime().atZone(ZoneOffset.UTC).format(EMAIL_DATETIME_FORMAT),
                    ticketDto.getFare(),
                    paymentStatus,
                    currentTime
            );

            helper.setText(plainTextContent, htmlContent);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Gửi email xác nhận đặt vé thất bại", e);
        }
    }

    /**
     * Generate HTML content for multi-passenger booking confirmation
     */
    private String generateSinglePassengerHtml(
            String customerName,
            String confirmationCode,
            String flightCode,
            String departureCity,
            String arrivalCity,
            String departureTime,
            BigDecimal fare,
            String paymentStatus,
            String currentTime
    ) {

        String statusColor = paymentStatus.contains("Chờ") ? "#ffc107" : "#28a745";
        String statusTextColor = paymentStatus.contains("Chờ") ? "black" : "white";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Xác nhận đặt vé - %s</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <div style="max-width: 700px; margin: 0 auto; background-color: #ffffff;">

                    <!-- Header -->
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">
                             ✈️ Flight Management
                        </h1>
                        <p style="color: rgba(255,255,255,0.8); margin: 5px 0 0 0; font-size: 12px;">
                             Đặt vé: %s UTC
                        </p>
                    </div>

                    <!-- Success Message -->
                    <div style="padding: 30px; text-align: center; background-color: #d4edda; border-bottom: 1px solid #c3e6cb;">
                        <div style="font-size: 48px; margin-bottom: 15px;">🎫</div>
                        <h2 style="color: #155724; margin: 0 0 15px 0;">Đặt vé thành công!</h2>
                        <p style="color: #155724; margin: 0;">
                             Kính chào %s, vé của bạn đã được đặt thành công
                        </p>
                        <div style="margin: 15px 0;">
                            <span style="background-color: %s; color: %s; padding: 10px 20px; border-radius: 25px; font-weight: bold; font-size: 14px;">
                                %s
                            </span>
                        </div>
                    </div>

                    <!-- Booking Information -->
                    <div style="padding: 30px;">
                        <h3 style="color: #007bff; margin-bottom: 20px; font-size: 18px; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
                             🎫 Thông tin đặt vé
                        </h3>

                        <table style="width: 100%%; border-collapse: collapse; margin-bottom: 30px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-weight: bold;">Mã xác nhận:</td>
                                <td style="padding: 15px; border-bottom: 1px solid #dee2e6; font-family: monospace; font-size: 16px; font-weight: bold; color: #007bff;">%s</td>
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
                            <tr style="background-color: #d4edda;">
                                <td style="padding: 15px; font-weight: bold; font-size: 16px;">Giá vé:</td>
                                <td style="padding: 15px; font-size: 18px; font-weight: bold; color: #28a745;">%s VND</td>
                            </tr>
                        </table>
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
            """,
                confirmationCode,
                currentTime,
                customerName,
                statusColor, statusTextColor, paymentStatus,
                confirmationCode, flightCode, departureCity, arrivalCity, departureTime,
                fare
        );
    }

}