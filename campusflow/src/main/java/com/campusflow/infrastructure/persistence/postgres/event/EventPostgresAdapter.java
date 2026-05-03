package com.campusflow.infrastructure.persistence.postgres.event;

import com.campusflow.domain.event.model.Event;
import com.campusflow.domain.event.port.EventRepositoryPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventPostgresAdapter implements EventRepositoryPort {
    private final EventJpaRepository eventJpaRepository;
    private final EventRsvpJpaRepository eventRsvpJpaRepository;

    @Override
    public Event save(Event event) {
        EventJpaEntity saved = eventJpaRepository.save(EventJpaEntity.fromDomain(event));
        return saved.toDomain();
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventJpaRepository.findById(id).map(EventJpaEntity::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return eventJpaRepository.findAllEvents()
                .stream()
                .map(EventJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Event> findUpcoming() {
        return eventJpaRepository.findUpcoming(LocalDateTime.now())
                .stream()
                .map(EventJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Event> searchByKeywordAll(String keyword) {
        return eventJpaRepository.searchByKeywordAll(keyword)
                .stream()
                .map(EventJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Event> searchByKeyword(String keyword) {
        return eventJpaRepository.searchByKeyword(keyword)
                .stream()
                .map(EventJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsRsvp(Long userId, Long eventId) {
        return eventRsvpJpaRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public void saveRsvp(Long userId, Long eventId) {
        EventRsvpEntity entity = new EventRsvpEntity();
        entity.setUserId(userId);
        entity.setEventId(eventId);
        eventRsvpJpaRepository.save(entity);
    }

    @Override
    public void deleteRsvp(Long userId, Long eventId) {
        eventRsvpJpaRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    @Override
    public int countRsvp(Long eventId) {
        return eventRsvpJpaRepository.countByEventId(eventId);
    }
}
