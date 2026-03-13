package com.campusflow.domain.event.exception;

public class RsvpNotFoundException extends RuntimeException {
    public RsvpNotFoundException() {
        super("RSVP not found for this user and event");
    }
}
