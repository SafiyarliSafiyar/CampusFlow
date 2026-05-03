package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.RegisterUserInput;
import com.campusflow.domain.user.model.User;

public interface RegisterUserUseCase {
    User register(RegisterUserInput input);
}
