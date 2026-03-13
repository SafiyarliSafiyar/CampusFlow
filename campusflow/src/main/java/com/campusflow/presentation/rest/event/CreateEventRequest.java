package com.campusflow.presentation.rest.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateEventRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    @NotBlank
    private String location;

    @Min(1)
    private int capacity;
}
