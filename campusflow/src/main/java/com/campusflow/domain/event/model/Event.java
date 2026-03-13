package com.campusflow.domain.event.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Event {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime eventDate;
    private final String location;
    private final int capacity;
    private final int rsvpCount;
    private final Long organizerId;
    private final EventStatus status;
    private final LocalDateTime createdAt;

    public boolean hasCapacity() {
        return rsvpCount < capacity;
    }

    public boolean isUpcoming() {
        return status == EventStatus.UPCOMING;
    }
}
