package com.campusflow.application.user.service;

import com.campusflow.application.user.dto.ProfileCompletenessResult;
import com.campusflow.application.user.usecase.GetProfileCompletenessUseCase;
import com.campusflow.domain.user.exception.UserNotFoundException;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.util.ArrayList;
import java.util.List;

public class GetProfileCompletenessService implements GetProfileCompletenessUseCase {
    private final UserRepositoryPort userRepositoryPort;

    public GetProfileCompletenessService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public ProfileCompletenessResult getForUser(Long userId) {
        User user = userRepositoryPort.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<String> missing = new ArrayList<>();
        int total = 4;
        int filled = 0;

        if (isFilled(user.getUsername())) {
            filled++;
        } else {
            missing.add("username");
        }
        if (isFilled(user.getMajor())) {
            filled++;
        } else {
            missing.add("major");
        }
        if (isFilled(user.getInterests())) {
            filled++;
        } else {
            missing.add("interests");
        }
        if (isFilled(user.getProfilePhotoUrl())) {
            filled++;
        } else {
            missing.add("profilePhotoUrl");
        }

        int percent = (int) Math.round((filled * 100.0) / total);
        return new ProfileCompletenessResult(percent, missing);
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

