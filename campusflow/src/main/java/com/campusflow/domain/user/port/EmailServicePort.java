package com.campusflow.domain.user.port;

public interface EmailServicePort {
    void sendOtpEmail(String toEmail, String otpCode);

    void sendPasswordResetEmail(String toEmail, String otpCode);

    void sendRsvpConfirmationEmail(String toEmail, String eventTitle, String eventDate, String location);
}
