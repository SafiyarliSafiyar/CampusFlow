package com.campusflow.application.user.dto;

import lombok.Value;

@Value
public class VerifyOtpInput {
    String email;
    String otpCode;
}
