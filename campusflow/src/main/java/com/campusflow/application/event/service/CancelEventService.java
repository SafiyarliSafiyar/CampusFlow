package com.campusflow.application.event.service;

import com.campusflow.application.event.usecase.CancelEventUseCase;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.model.EventStatus;
import com.campusflow.domain.event.port.EventRepositoryPort;

public class CancelEventService implements CancelEventUseCase {
    private final EventRepositoryPort eventRepositoryPort;

    public CancelEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public void cancel(Long eventId) {
        Event existing = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Event cancelled = Event.builder()
                .id(existing.getId())
                .title(existing.getTitle())
                .description(existing.getDescription())
                .eventDate(existing.getEventDate())
                .location(existing.getLocation())
                .latitude(existing.getLatitude())
                .longitude(existing.getLongitude())
                .capacity(existing.getCapacity())
                .rsvpCount(existing.getRsvpCount())
                .organizerId(existing.getOrganizerId())
                .status(EventStatus.CANCELLED)
                .createdAt(existing.getCreatedAt())
                .build();

        eventRepositoryPort.save(cancelled);
    }
}
