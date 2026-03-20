package com.campusflow.infrastructure.persistence.postgres.studygroup;

import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestJpaRepository extends JpaRepository<JoinRequestJpaEntity, Long> {
    Optional<JoinRequestJpaEntity> findByUserIdAndStudyGroupIdAndStatus(
            Long userId,
            Long studyGroupId,
            JoinRequestStatus status
    );

    List<JoinRequestJpaEntity> findByStudyGroupId(Long studyGroupId);

    Optional<JoinRequestJpaEntity> findByUserIdAndStudyGroupId(Long userId, Long studyGroupId);
}
