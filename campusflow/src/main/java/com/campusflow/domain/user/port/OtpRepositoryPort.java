package com.campusflow.domain.user.port;

import com.campusflow.domain.user.model.OtpPurpose;

public interface OtpRepositoryPort {
    void saveOtp(String email, OtpPurpose purpose, String otpCode);

    boolean verifyOtp(String email, OtpPurpose purpose, String otpCode);

    void deleteOtp(String email, OtpPurpose purpose);

    boolean hasOtp(String email, OtpPurpose purpose);
}
