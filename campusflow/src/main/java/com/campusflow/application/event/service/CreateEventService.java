package com.campusflow.application.event.service;

import com.campusflow.application.event.dto.CreateEventInput;
import com.campusflow.application.event.dto.EventResult;
import com.campusflow.application.event.usecase.CreateEventUseCase;
import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.model.EventStatus;
import com.campusflow.domain.event.port.EventRepositoryPort;
import java.time.LocalDateTime;

public class CreateEventService implements CreateEventUseCase {
    private final EventRepositoryPort eventRepositoryPort;

    public CreateEventService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public EventResult create(CreateEventInput input) {
        Event event = Event.builder()
                .id(null)
                .title(input.getTitle())
                .description(input.getDescription())
                .eventDate(input.getEventDate())
                .location(input.getLocation())
                .latitude(input.getLatitude())
                .longitude(input.getLongitude())
                .capacity(input.getCapacity())
                .rsvpCount(0)
                .organizerId(input.getOrganizerId())
                .status(EventStatus.UPCOMING)
                .createdAt(LocalDateTime.now())
                .build();

        Event saved = eventRepositoryPort.save(event);
        return EventResultMapper.toResult(saved);
    }
}
