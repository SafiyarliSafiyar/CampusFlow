package com.campusflow.domain.user.exception;

public class InvalidEmailDomainException extends RuntimeException {
    public InvalidEmailDomainException(String email) {
        super("Only ADA University email addresses are allowed. Got: " + email);
    }
}
