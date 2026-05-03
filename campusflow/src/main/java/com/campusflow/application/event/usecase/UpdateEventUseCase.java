package com.campusflow.application.event.usecase;

import com.campusflow.application.event.dto.EventResult;
import com.campusflow.application.event.dto.UpdateEventInput;

public interface UpdateEventUseCase {
    EventResult update(Long eventId, UpdateEventInput input);
}
