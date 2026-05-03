package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.ProfileCompletenessResult;

public interface GetProfileCompletenessUseCase {
    ProfileCompletenessResult getForUser(Long userId);
}

