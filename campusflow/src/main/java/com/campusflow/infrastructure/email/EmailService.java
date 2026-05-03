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
        sendEmail(
                toEmail,
                "CampusFlow - Email Verification",
                "Your CampusFlow verification code is: " + otpCode + "\n\n"
                        + "This code expires in 10 minutes.\n"
                        + "If you did not register, please ignore this email."
        );
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String otpCode) {
        sendEmail(
                toEmail,
                "CampusFlow - Password Reset",
                "Your CampusFlow password reset code is: " + otpCode + "\n\n"
                        + "This code expires in 10 minutes.\n"
                        + "If you did not request a password reset, please ignore this email."
        );
    }

    @Override
    public void sendRsvpConfirmationEmail(String toEmail, String eventTitle, String eventDate, String location) {
        sendEmail(
                toEmail,
                "CampusFlow - RSVP Confirmed",
                "Your RSVP is confirmed.\n\n"
                        + "Event: " + eventTitle + "\n"
                        + "When: " + eventDate + "\n"
                        + "Where: " + location + "\n\n"
                        + "See you there!"
        );
    }

    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("safiyarlisafiyar@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
