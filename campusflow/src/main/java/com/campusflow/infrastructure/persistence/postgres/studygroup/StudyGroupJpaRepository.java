package com.campusflow.infrastructure.persistence.postgres.studygroup;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyGroupJpaRepository extends JpaRepository<StudyGroupJpaEntity, Long> {
    @Query("SELECT g FROM StudyGroupJpaEntity g WHERE g.status = 'OPEN'")
    List<StudyGroupJpaEntity> findAllOpen();

    @Query("SELECT g FROM StudyGroupJpaEntity g WHERE "
            + "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(g.topic) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(g.course) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StudyGroupJpaEntity> searchByKeyword(@Param("keyword") String keyword);
}
