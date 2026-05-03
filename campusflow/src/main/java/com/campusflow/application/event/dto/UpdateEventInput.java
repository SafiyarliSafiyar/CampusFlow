package com.campusflow.application.event.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class UpdateEventInput {
    String title;
    String description;
    LocalDateTime eventDate;
    String location;
    Double latitude;
    Double longitude;
    int capacity;
}
