package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.repository.TokenRepository;
import com.marvin.campustrade.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TokenRepository tokenRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, Token token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String html = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background: #f9f9f9;">
                    <div style="max-width: 500px; margin: auto; background: white; padding: 20px; 
                                border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">

                        <h2 style="text-align: center; color: #4A90E2;">Verify Your Student Email</h2>

                        <p>Hello,</p>
                        <p>Thank you for registering at <strong>CampusTrade</strong>.</p>
                        <p>Please use the following verification code to verify your student email:</p>

                        <div style="text-align: center; margin: 20px 0;">
                            <div style="font-size: 28px; font-weight: bold; 
                                        letter-spacing: 4px; color: #4A90E2;">
                                %s
                            </div>
                        </div>

                        <p style="font-size: 14px; color: #555;">
                            Enter this code in the CampusTrade app to complete your registration.
                        </p>

                        <br>
                        <p style="font-size: 12px; color: #888; text-align: center;">
                            This email was sent automatically by CampusTrade.
                        </p>
                    </div>
                </div>
                """.formatted(token.getContent());

            helper.setTo(to);
            helper.setSubject("CampusTrade – Your Verification Code");
            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendResetEmail(String email, Token token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String html = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background: #f9f9f9;">
                    <div style="max-width: 500px; margin: auto; background: white; padding: 20px; 
                                border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                
                        <h2 style="text-align: center; color: #4A90E2;">Reset Your Password</h2>
                
                        <p>Hello,</p>
                        <p>We received a request to reset your <strong>CampusTrade</strong> password.</p>
                        <p>Please use the following code to reset your password:</p>
                
                        <div style="text-align: center; margin: 20px 0;">
                            <div style="font-size: 32px; font-weight: bold; 
                                        letter-spacing: 6px; color: #4A90E2;">
                                %s
                            </div>
                        </div>
                
                        <p style="font-size: 14px; color: #555;">
                            Enter this code in the CampusTrade app to set a new password.
                        </p>
                
                        <p style="font-size: 14px; color: #555;">
                            If you did not request a password reset, you can safely ignore this email.
                        </p>
                
                        <br>
                        <p style="font-size: 12px; color: #888; text-align: center;">
                            This reset code will expire in 15 minutes.<br>
                            This email was sent automatically by CampusTrade.
                        </p>
                    </div>
                </div>
                """.formatted(token.getContent());
            helper.setTo(email);
            helper.setSubject("Reset Your Password - CampusTrade");
            helper.setText(html, true);  // true = HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }
}
