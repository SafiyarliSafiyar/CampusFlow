package com.campusflow.presentation.rest.user;

import com.campusflow.domain.user.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotNull
    private UserRole role;
}
