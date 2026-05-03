package com.campusflow.application.event.service;

import com.campusflow.application.event.dto.EventResult;
import com.campusflow.application.event.dto.UpdateEventInput;
import com.campusflow.application.event.usecase.UpdateEventUseCase;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.port.EventRepositoryPort;

public class UpdateEventService implements UpdateEventUseCase {
    private final EventRepositoryPort eventRepositoryPort;

    public UpdateEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public EventResult update(Long eventId, UpdateEventInput input) {
        Event existing = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Event updated = Event.builder()
                .id(existing.getId())
                .title(input.getTitle())
                .description(input.getDescription())
                .eventDate(input.getEventDate())
                .location(input.getLocation())
                .latitude(input.getLatitude() == null ? existing.getLatitude() : input.getLatitude())
                .longitude(input.getLongitude() == null ? existing.getLongitude() : input.getLongitude())
                .capacity(input.getCapacity())
                .rsvpCount(existing.getRsvpCount())
                .organizerId(existing.getOrganizerId())
                .status(existing.getStatus())
                .createdAt(existing.getCreatedAt())
                .build();

        Event saved = eventRepositoryPort.save(updated);
        return EventResultMapper.toResult(saved);
    }
}
