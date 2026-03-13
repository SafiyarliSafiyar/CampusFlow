package com.campusflow.domain.event.exception;

public class AlreadyRsvpedException extends RuntimeException {
    public AlreadyRsvpedException() {
        super("User has already RSVP'd to this event");
    }
}
