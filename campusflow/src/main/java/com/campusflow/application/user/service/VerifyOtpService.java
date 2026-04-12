package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.VerifyOtpInput;
import com.campusflow.application.user.usecase.VerifyOtpUseCase;
import com.campusflow.domain.user.exception.InvalidOtpException;
import com.campusflow.domain.user.exception.OtpNotFoundException;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.model.VerificationStatus;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import com.campusflow.domain.user.port.UserRepositoryPort;

public class VerifyOtpService implements VerifyOtpUseCase {
    private final OtpRepositoryPort otpRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public VerifyOtpService(OtpRepositoryPort otpRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.otpRepositoryPort = otpRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void verifyOtp(VerifyOtpInput input) {
        User user = userRepositoryPort.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException(input.getEmail()));

        if (!otpRepositoryPort.hasOtp(input.getEmail())) {
            throw new OtpNotFoundException();
        }

        if (!otpRepositoryPort.verifyOtp(input.getEmail(), input.getOtpCode())) {
            throw new InvalidOtpException();
        }

        User verifiedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .verificationStatus(VerificationStatus.VERIFIED)
                .createdAt(user.getCreatedAt())
                .build();

        userRepositoryPort.save(verifiedUser);
        otpRepositoryPort.deleteOtp(input.getEmail());
    }
}
