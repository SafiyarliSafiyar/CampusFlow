package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.UpdateProfileInput;
import com.campusflow.application.user.usecase.UpdateProfileUseCase;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.UserRepositoryPort;

public class UpdateProfileService implements UpdateProfileUseCase {
    private final UserRepositoryPort userRepositoryPort;

    public UpdateProfileService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileInput input) {
        User existingUser = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .username(input.getUsername())
                .email(existingUser.getEmail())
                .major(input.getMajor() == null ? existingUser.getMajor() : input.getMajor())
                .interests(input.getInterests() == null ? existingUser.getInterests() : input.getInterests())
                .profilePhotoUrl(input.getProfilePhotoUrl() == null ? existingUser.getProfilePhotoUrl() : input.getProfilePhotoUrl())
                .visibility(input.getVisibility() == null ? existingUser.getVisibility() : input.getVisibility())
                .passwordHash(existingUser.getPasswordHash())
                .role(existingUser.getRole())
                .verificationStatus(existingUser.getVerificationStatus())
                .createdAt(existingUser.getCreatedAt())
                .build();

        return userRepositoryPort.save(updatedUser);
    }
}
