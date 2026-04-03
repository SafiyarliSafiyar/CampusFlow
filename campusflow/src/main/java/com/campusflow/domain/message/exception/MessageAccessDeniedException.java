package com.campusflow.domain.message.exception;

public class MessageAccessDeniedException extends RuntimeException {
    public MessageAccessDeniedException() {
        super("You must be a member of this group to access messages");
    }
}
