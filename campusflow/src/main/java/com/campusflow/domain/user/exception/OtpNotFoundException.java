package com.campusflow.domain.user.exception;

public class OtpNotFoundException extends RuntimeException {
    public OtpNotFoundException() {
        super("No OTP found for this email");
    }
}
