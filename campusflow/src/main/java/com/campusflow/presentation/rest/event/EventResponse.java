package com.campusflow.presentation.rest.event;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class EventResponse {
    Long id;
    String title;
    String description;
    LocalDateTime eventDate;
    String location;
    int capacity;
    int rsvpCount;
    Long organizerId;
    String status;
    LocalDateTime createdAt;
}
