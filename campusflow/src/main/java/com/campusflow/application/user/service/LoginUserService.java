package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.LoginResult;
import com.campusflow.application.user.dto.LoginUserInput;
import com.campusflow.application.user.usecase.LoginUserUseCase;
import com.campusflow.domain.user.exception.InvalidCredentialsException;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LoginUserService implements LoginUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderPort tokenProviderPort;

    public LoginUserService(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            TokenProviderPort tokenProviderPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public LoginResult login(LoginUserInput input) {
        User user = userRepositoryPort.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException(input.getEmail()));

        if (!passwordEncoder.matches(input.getRawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenProviderPort.generateToken(user);
        return new LoginResult(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
