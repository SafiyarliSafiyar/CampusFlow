package com.campusflow.infrastructure.persistence.postgres.studygroup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMemberEntity, Long> {
    boolean existsByStudyGroupIdAndUserId(Long studyGroupId, Long userId);

    void deleteByStudyGroupIdAndUserId(Long studyGroupId, Long userId);
}
