package com.flightmanagement.service.impl;

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

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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

            // HTML version (your existing content)
            String htmlContent = String.format("""
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
                    """, resetLink, resetLink, currentTime);

            // Set both text and HTML content
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
