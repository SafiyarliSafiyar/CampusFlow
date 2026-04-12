package com.campusflow.domain.user.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Email not verified. Please verify your email first.");
    }
}
