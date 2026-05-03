package com.campusflow.application.event.service;

import com.campusflow.application.event.dto.EventResult;
import com.campusflow.application.event.usecase.GetEventsUseCase;
import com.campusflow.domain.event.exception.EventNotFoundException;
import com.campusflow.domain.event.port.EventRepositoryPort;
import java.util.List;

public class GetEventsService implements GetEventsUseCase {
    private final EventRepositoryPort eventRepositoryPort;

    public GetEventsService(EventRepositoryPort eventRepositoryPort) {
        this.eventRepositoryPort = eventRepositoryPort;
    }

    @Override
    public EventResult getById(Long id) {
        return eventRepositoryPort.findById(id)
                .map(EventResultMapper::toResult)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @Override
    public List<EventResult> getAllIncludingCancelled() {
        return eventRepositoryPort.findAll()
                .stream()
                .map(EventResultMapper::toResult)
                .toList();
    }

    @Override
    public List<EventResult> getUpcoming() {
        return eventRepositoryPort.findUpcoming()
                .stream()
                .map(EventResultMapper::toResult)
                .toList();
    }

    @Override
    public List<EventResult> searchAll(String keyword) {
        return eventRepositoryPort.searchByKeywordAll(keyword)
                .stream()
                .map(EventResultMapper::toResult)
                .toList();
    }

    @Override
    public List<EventResult> search(String keyword) {
        return eventRepositoryPort.searchByKeyword(keyword)
                .stream()
                .map(EventResultMapper::toResult)
                .toList();
    }
}
