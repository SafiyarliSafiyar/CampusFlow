package com.campusflow.application.event.service;

import com.campusflow.application.event.dto.EventResult;
import com.campusflow.domain.event.model.Event;

final class EventResultMapper {
    private EventResultMapper() {
    }

    static EventResult toResult(Event event) {
        return new EventResult(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getLatitude(),
                event.getLongitude(),
                event.getCapacity(),
                event.getRsvpCount(),
                event.getOrganizerId(),
                event.getStatus().name(),
                event.getCreatedAt()
        );
    }
}
