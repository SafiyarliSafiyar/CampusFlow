package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.AssignRoleInput;
import com.campusflow.application.user.usecase.AssignRoleUseCase;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.UserRepositoryPort;

public class AssignRoleService implements AssignRoleUseCase {
    private final UserRepositoryPort userRepositoryPort;

    public AssignRoleService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User assignRole(AssignRoleInput input) {
        User existingUser = userRepositoryPort.findById(input.getUserId())
                .orElseThrow(() -> new UserNotFoundException(input.getUserId()));

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .passwordHash(existingUser.getPasswordHash())
                .role(input.getRole())
                .createdAt(existingUser.getCreatedAt())
                .build();

        return userRepositoryPort.save(updatedUser);
    }
}
