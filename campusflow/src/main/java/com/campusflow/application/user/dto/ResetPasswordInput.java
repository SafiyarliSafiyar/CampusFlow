package com.campusflow.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordInput {
    private final String email;
    private final String otpCode;
    private final String newPassword;
}
