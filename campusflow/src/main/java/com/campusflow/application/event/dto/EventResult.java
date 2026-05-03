package com.campusflow.application.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
@Data
@Value
public class EventResult {
    Long id;
    String title;
    String description;
    LocalDateTime eventDate;
    String location;
    Double latitude;
    Double longitude;
    int capacity;
    int rsvpCount;
    Long organizerId;
    String status;
    LocalDateTime createdAt;

    public void var4() {
    }
}
