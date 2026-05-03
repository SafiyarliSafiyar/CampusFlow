package com.campusflow.presentation.rest.user;

import com.campusflow.application.user.dto.AssignRoleInput;
import com.campusflow.application.user.dto.LoginResult;
import com.campusflow.application.user.dto.LoginUserInput;
import com.campusflow.application.user.dto.RegisterUserInput;
import com.campusflow.application.user.dto.ResetPasswordInput;
import com.campusflow.application.user.dto.UpdateProfileInput;
import com.campusflow.application.user.dto.VerifyOtpInput;
import com.campusflow.application.user.usecase.AssignRoleUseCase;
import com.campusflow.application.user.usecase.LoginUserUseCase;
import com.campusflow.application.user.usecase.RegisterUserUseCase;
import com.campusflow.application.user.usecase.RequestPasswordResetUseCase;
import com.campusflow.application.user.usecase.ResetPasswordUseCase;
import com.campusflow.application.user.usecase.SendOtpUseCase;
import com.campusflow.application.user.usecase.UpdateProfileUseCase;
import com.campusflow.application.user.usecase.VerifyOtpUseCase;
import com.campusflow.application.user.usecase.GetProfileCompletenessUseCase;
import com.campusflow.application.user.dto.ProfileCompletenessResult;
import com.campusflow.domain.user.model.User;
import com.campusflow.infrastructure.storage.LocalUploadsStorage;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GetProfileCompletenessUseCase getProfileCompletenessUseCase;
    private final LocalUploadsStorage localUploadsStorage;

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

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody @Valid VerifyOtpRequest request) {
        verifyOtpUseCase.verifyOtp(new VerifyOtpInput(request.getEmail(), request.getOtpCode()));
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
        sendOtpUseCase.sendOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid ResendOtpRequest request) {
        requestPasswordResetUseCase.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message",
                "If the account exists, a password reset code has been sent."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(new ResetPasswordInput(
                request.getEmail(),
                request.getOtpCode(),
                request.getNewPassword()
        ));
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        User updatedUser = updateProfileUseCase.updateProfile(
                extractUserId(),
                new UpdateProfileInput(
                        request.getUsername(),
                        request.getMajor(),
                        request.getInterests(),
                        request.getProfilePhotoUrl(),
                        request.getVisibility()
                )
        );

        UserResponse response = new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getMajor(),
                updatedUser.getInterests(),
                updatedUser.getProfilePhotoUrl(),
                updatedUser.getVisibility() == null ? null : updatedUser.getVisibility().name(),
                updatedUser.getRole().name(),
                updatedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/completeness")
    public ResponseEntity<ProfileCompletenessResponse> getProfileCompleteness() {
        ProfileCompletenessResult result = getProfileCompletenessUseCase.getForUser(extractUserId());
        return ResponseEntity.ok(new ProfileCompletenessResponse(result.getPercent(), result.getMissingFields()));
    }

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> uploadProfilePhoto(@RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        if (file.getSize() > 5L * 1024L * 1024L) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        String uploadedUrl = localUploadsStorage.storeProfilePhoto(file);

        User updatedUser = updateProfileUseCase.updateProfile(
                extractUserId(),
                new UpdateProfileInput(
                        null,
                        null,
                        null,
                        uploadedUrl,
                        null
                )
        );

        UserResponse response = new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getMajor(),
                updatedUser.getInterests(),
                updatedUser.getProfilePhotoUrl(),
                updatedUser.getVisibility() == null ? null : updatedUser.getVisibility().name(),
                updatedUser.getRole().name(),
                updatedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterUserResponse> assignRole(
            @PathVariable Long id,
            @RequestBody @Valid AssignRoleRequest request
    ) {
        User updatedUser = assignRoleUseCase.assignRole(new AssignRoleInput(id, request.getRole()));

        RegisterUserResponse response = new RegisterUserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole().name(),
                updatedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException(
                "User id is missing from authentication context");
    }
}
