package com.campusflow.domain.studygroup.exception;

public class NotGroupCreatorException extends RuntimeException {
    public NotGroupCreatorException() {
        super("Only the group creator can perform this action");
    }
}
