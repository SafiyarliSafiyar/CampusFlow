package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.LoginResult;
import com.campusflow.application.user.dto.LoginUserInput;

public interface LoginUserUseCase {
    LoginResult login(LoginUserInput input);
}
