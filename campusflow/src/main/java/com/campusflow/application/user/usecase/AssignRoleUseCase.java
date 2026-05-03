package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.AssignRoleInput;
import com.campusflow.domain.user.model.User;

public interface AssignRoleUseCase {
    User assignRole(AssignRoleInput input);
}
