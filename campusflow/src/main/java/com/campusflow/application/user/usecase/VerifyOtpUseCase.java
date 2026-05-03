package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.VerifyOtpInput;

public interface VerifyOtpUseCase {
    void verifyOtp(VerifyOtpInput input);
}
