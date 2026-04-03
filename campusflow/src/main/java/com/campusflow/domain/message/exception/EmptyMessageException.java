package com.campusflow.domain.message.exception;

public class EmptyMessageException extends RuntimeException {
    public EmptyMessageException() {
        super("Message content cannot be empty");
    }
}
