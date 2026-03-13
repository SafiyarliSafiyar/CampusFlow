package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.RegisterUserInput;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.domain.user.exception.EmailAlreadyExistsException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.model.UserRole;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterUserInput input) {
        if (userRepositoryPort.existsByEmail(input.getEmail())) {
            throw new EmailAlreadyExistsException(input.getEmail());
        }

        User user = User.builder()
                .id(null)
                .username(input.getUsername())
                .email(input.getEmail())
                .passwordHash(passwordEncoder.encode(input.getRawPassword()))
                .role(UserRole.STUDENT)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepositoryPort.save(user);
    }
}
