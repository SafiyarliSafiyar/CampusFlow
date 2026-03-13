package com.campusflow.infrastructure.persistence.postgres.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRsvpJpaRepository extends JpaRepository<EventRsvpEntity, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    void deleteByUserIdAndEventId(Long userId, Long eventId);

    int countByEventId(Long eventId);
}
