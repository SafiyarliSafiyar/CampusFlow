package com.campusflow.domain.event.exception;

public class EventCancelledException extends RuntimeException {
    public EventCancelledException() {
        super("Event has been cancelled");
    }
}
