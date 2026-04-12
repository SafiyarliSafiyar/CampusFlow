package com.campusflow.infrastructure.email;

import com.campusflow.domain.user.port.EmailServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServicePort {
    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("safiyarlisafiyar@gmail.com");
        message.setTo(toEmail);
        message.setSubject("CampusFlow - Email Verification");
        message.setText("Your CampusFlow verification code is: " + otpCode + "\n\n"
                + "This code expires in 10 minutes.\n"
                + "If you did not register, please ignore this email.");
        mailSender.send(message);
    }
}
