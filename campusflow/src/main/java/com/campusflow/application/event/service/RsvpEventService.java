package com.campusflow.application.event.service;

import com.campusflow.application.event.usecase.RsvpEventUseCase;
import com.campusflow.domain.event.exception.AlreadyRsvpedException;
import com.campusflow.domain.event.exception.EventCancelledException;
import com.campusflow.domain.event.exception.EventFullException;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.exception.RsvpNotFoundException;
import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.port.EventRepositoryPort;

public class RsvpEventService implements RsvpEventUseCase {
    private final EventRepositoryPort eventRepositoryPort;

    public RsvpEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public void rsvp(Long userId, Long eventId) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.isUpcoming()) {
            throw new EventCancelledException();
        }
        if (!event.hasCapacity()) {
            throw new EventFullException();
        }
        if (eventRepositoryPort.existsRsvp(userId, eventId)) {
            throw new AlreadyRsvpedException();
        }

        eventRepositoryPort.saveRsvp(userId, eventId);
        Event updated = Event.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .rsvpCount(event.getRsvpCount() + 1)
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
        eventRepositoryPort.save(updated);
    }

    @Override
    public void cancelRsvp(Long userId, Long eventId) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!eventRepositoryPort.existsRsvp(userId, eventId)) {
            throw new RsvpNotFoundException();
        }

        eventRepositoryPort.deleteRsvp(userId, eventId);
        int nextCount = Math.max(0, event.getRsvpCount() - 1);
        Event updated = Event.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .rsvpCount(nextCount)
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
        eventRepositoryPort.save(updated);
    }
}
