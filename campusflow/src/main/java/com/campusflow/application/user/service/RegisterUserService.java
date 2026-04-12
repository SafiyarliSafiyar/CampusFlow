package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.RegisterUserInput;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.application.user.usecase.SendOtpUseCase;
import com.campusflow.domain.user.exception.EmailAlreadyExistsException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.model.UserRole;
import com.campusflow.domain.user.model.VerificationStatus;
import com.campusflow.domain.user.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final SendOtpUseCase sendOtpUseCase;

    public RegisterUserService(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            SendOtpUseCase sendOtpUseCase
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.sendOtpUseCase = sendOtpUseCase;
    }

    @Override
    public User register(RegisterUserInput input) {
        User existingUser = userRepositoryPort.findByEmail(input.getEmail()).orElse(null);

        if (existingUser != null) {
            if (existingUser.getVerificationStatus() == VerificationStatus.PENDING) {
                sendOtpUseCase.sendOtp(existingUser.getEmail());
                return existingUser;
            }
            throw new EmailAlreadyExistsException(input.getEmail());
        }

        User user = User.builder()
                .id(null)
                .username(input.getUsername())
                .email(input.getEmail())
                .passwordHash(passwordEncoder.encode(input.getRawPassword()))
                .role(UserRole.STUDENT)
                .verificationStatus(VerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepositoryPort.save(user);
        sendOtpUseCase.sendOtp(savedUser.getEmail());
        return savedUser;
    }
}
