package com.campusflow.domain.user.port;

public interface OtpRepositoryPort {
    void saveOtp(String email, String otpCode);

    boolean verifyOtp(String email, String otpCode);

    void deleteOtp(String email);

    boolean hasOtp(String email);
}
