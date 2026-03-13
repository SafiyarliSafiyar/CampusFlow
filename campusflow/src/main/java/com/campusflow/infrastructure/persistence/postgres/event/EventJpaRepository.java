package com.campusflow.infrastructure.persistence.postgres.event;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {
    @Query("SELECT e FROM EventJpaEntity e ORDER BY e.eventDate ASC")
    List<EventJpaEntity> findAllEvents();

    @Query("SELECT e FROM EventJpaEntity e WHERE e.eventDate > :now "
            + "AND e.status = 'UPCOMING' ORDER BY e.eventDate ASC")
    List<EventJpaEntity> findUpcoming(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM EventJpaEntity e WHERE "
            + "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EventJpaEntity> searchByKeywordAll(@Param("keyword") String keyword);

    @Query("SELECT e FROM EventJpaEntity e WHERE "
            + "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND e.status = 'UPCOMING'")
    List<EventJpaEntity> searchByKeyword(@Param("keyword") String keyword);
}
