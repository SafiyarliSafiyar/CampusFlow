package com.campusflow.presentation.rest.user;

import com.campusflow.application.user.dto.LoginResult;
import com.campusflow.application.user.dto.LoginUserInput;
import com.campusflow.application.user.dto.RegisterUserInput;
import com.campusflow.application.user.dto.UpdateProfileInput;
import com.campusflow.application.user.usecase.LoginUserUseCase;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.application.user.usecase.UpdateProfileUseCase;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.UserRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UserRepositoryPort userRepositoryPort;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        User user = registerUserUseCase.register(new RegisterUserInput(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        ));

        RegisterUserResponse response = new RegisterUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResult result = loginUserUseCase.login(new LoginUserInput(
                request.getEmail(),
                request.getPassword()
        ));

        LoginResponse response = new LoginResponse(
                result.getToken(),
                result.getUserId(),
                result.getUsername(),
                result.getEmail(),
                result.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Authentication required");
        }

        String email = authentication.getName();
        User currentUser = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));

        User updatedUser = updateProfileUseCase.updateProfile(
                currentUser.getId(),
                new UpdateProfileInput(request.getUsername())
        );

        UserResponse response = new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole().name(),
                updatedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}
