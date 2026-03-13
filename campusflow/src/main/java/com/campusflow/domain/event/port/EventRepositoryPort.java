package com.campusflow.domain.event.port;

import com.campusflow.domain.event.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    Event save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll();

    List<Event> findUpcoming();

    List<Event> searchByKeywordAll(String keyword);

    List<Event> searchByKeyword(String keyword);

    boolean existsRsvp(Long userId, Long eventId);

    void saveRsvp(Long userId, Long eventId);

    void deleteRsvp(Long userId, Long eventId);

    int countRsvp(Long eventId);
}
