package com.campusflow.application.user.usecase;

import com.campusflow.application.user.dto.ResetPasswordInput;

public interface ResetPasswordUseCase {
    void resetPassword(ResetPasswordInput input);
}
