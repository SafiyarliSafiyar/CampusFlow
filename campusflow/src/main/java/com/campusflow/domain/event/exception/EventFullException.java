package com.campusflow.domain.event.exception;

public class EventFullException extends RuntimeException {
    public EventFullException() {
        super("Event is at full capacity");
    }
}
