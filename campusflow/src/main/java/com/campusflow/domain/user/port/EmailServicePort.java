package com.campusflow.domain.user.port;

public interface EmailServicePort {
    void sendOtpEmail(String toEmail, String otpCode);
}
