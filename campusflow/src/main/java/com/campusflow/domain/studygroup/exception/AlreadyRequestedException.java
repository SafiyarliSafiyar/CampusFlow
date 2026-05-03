package com.campusflow.domain.studygroup.exception;

public class AlreadyRequestedException extends RuntimeException {
    public AlreadyRequestedException() {
        super("User already has a pending join request");
    }
}
