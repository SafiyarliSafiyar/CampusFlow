package com.campusflow.application.event.usecase;

import com.campusflow.application.event.dto.EventResult;
import java.util.List;

public interface GetEventsUseCase {
    EventResult getById(Long id);

    List<EventResult> getAllIncludingCancelled();

    List<EventResult> getUpcoming();

    List<EventResult> searchAll(String keyword);

    List<EventResult> search(String keyword);
}
