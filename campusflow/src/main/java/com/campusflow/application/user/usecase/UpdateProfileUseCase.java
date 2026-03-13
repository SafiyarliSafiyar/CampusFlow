package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.UpdateProfileInput;
import com.campusflow.domain.user.model.User;

public interface UpdateProfileUseCase {
    User updateProfile(Long userId, UpdateProfileInput input);
}
