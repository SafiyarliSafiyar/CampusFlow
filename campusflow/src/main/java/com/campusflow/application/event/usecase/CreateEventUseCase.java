package com.campusflow.application.event.usecase;

import com.campusflow.application.event.dto.CreateEventInput;
import com.campusflow.application.event.dto.EventResult;

public interface CreateEventUseCase {
    EventResult create(CreateEventInput input);
}
