package com.campusflow.application.user.service;

import com.campusflow.application.user.usecase.SendOtpUseCase;
import com.campusflow.domain.user.model.OtpPurpose;
import com.campusflow.domain.user.port.EmailServicePort;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import java.security.SecureRandom;

public class SendOtpService implements SendOtpUseCase {
    private final OtpRepositoryPort otpRepositoryPort;
    private final EmailServicePort emailServicePort;
    private final SecureRandom secureRandom = new SecureRandom();

    public SendOtpService(OtpRepositoryPort otpRepositoryPort, EmailServicePort emailServicePort) {
        this.otpRepositoryPort = otpRepositoryPort;
        this.emailServicePort = emailServicePort;
    }

    @Override
    public void sendOtp(String email) {
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));
        otpRepositoryPort.saveOtp(email, OtpPurpose.EMAIL_VERIFICATION, otpCode);
        emailServicePort.sendOtpEmail(email, otpCode);
    }
}
