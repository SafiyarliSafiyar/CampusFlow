package com.campusflow.application.user.dto;

import com.campusflow.domain.user.model.UserRole;
import lombok.Value;

@Value
public class AssignRoleInput {
    Long userId;
    UserRole role;
}
