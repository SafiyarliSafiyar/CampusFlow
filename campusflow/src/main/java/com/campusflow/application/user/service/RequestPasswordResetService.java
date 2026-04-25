package com.campusflow.application.user.service;

import com.campusflow.application.user.usecase.RequestPasswordResetUseCase;
import com.campusflow.domain.user.model.OtpPurpose;
import com.campusflow.domain.user.port.EmailServicePort;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.security.SecureRandom;

public class RequestPasswordResetService implements RequestPasswordResetUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final OtpRepositoryPort otpRepositoryPort;
    private final EmailServicePort emailServicePort;
    private final SecureRandom secureRandom = new SecureRandom();

    public RequestPasswordResetService(
            UserRepositoryPort userRepositoryPort,
            OtpRepositoryPort otpRepositoryPort,
            EmailServicePort emailServicePort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.otpRepositoryPort = otpRepositoryPort;
        this.emailServicePort = emailServicePort;
    }

    @Override
    public void requestReset(String email) {
        if (userRepositoryPort.findByEmail(email).isEmpty()) {
            return;
        }

        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));
        otpRepositoryPort.saveOtp(email, OtpPurpose.PASSWORD_RESET, otpCode);
        emailServicePort.sendPasswordResetEmail(email, otpCode);
    }
}
