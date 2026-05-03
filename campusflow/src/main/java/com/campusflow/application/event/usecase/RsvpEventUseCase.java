package com.campusflow.application.event.usecase;

public interface RsvpEventUseCase {
    void rsvp(Long userId, Long eventId);

    void cancelRsvp(Long userId, Long eventId);
}
