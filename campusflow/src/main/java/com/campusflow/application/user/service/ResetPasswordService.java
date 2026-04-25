package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.ResetPasswordInput;
import com.campusflow.application.user.usecase.ResetPasswordUseCase;
import com.campusflow.domain.user.exception.InvalidOtpException;
import com.campusflow.domain.user.exception.OtpNotFoundException;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.OtpPurpose;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ResetPasswordService implements ResetPasswordUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final OtpRepositoryPort otpRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(
            UserRepositoryPort userRepositoryPort,
            OtpRepositoryPort otpRepositoryPort,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.otpRepositoryPort = otpRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void resetPassword(ResetPasswordInput input) {
        User user = userRepositoryPort.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException(input.getEmail()));

        if (!otpRepositoryPort.hasOtp(input.getEmail(), OtpPurpose.PASSWORD_RESET)) {
            throw new OtpNotFoundException();
        }

        if (!otpRepositoryPort.verifyOtp(input.getEmail(), OtpPurpose.PASSWORD_RESET, input.getOtpCode())) {
            throw new InvalidOtpException();
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(passwordEncoder.encode(input.getNewPassword()))
                .role(user.getRole())
                .verificationStatus(user.getVerificationStatus())
                .createdAt(user.getCreatedAt())
                .build();

        userRepositoryPort.save(updatedUser);
        otpRepositoryPort.deleteOtp(input.getEmail(), OtpPurpose.PASSWORD_RESET);
    }
}
