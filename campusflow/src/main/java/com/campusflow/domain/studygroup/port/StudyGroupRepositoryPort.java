package com.campusflow.domain.studygroup.port;

import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.StudyGroup;
import java.util.List;
import java.util.Optional;

public interface StudyGroupRepositoryPort {
    StudyGroup save(StudyGroup studyGroup);

    Optional<StudyGroup> findById(Long id);

    List<StudyGroup> findAllOpen();

    List<StudyGroup> searchByKeyword(String keyword);

    JoinRequest saveJoinRequest(JoinRequest joinRequest);

    Optional<JoinRequest> findJoinRequestById(Long requestId);

    Optional<JoinRequest> findPendingRequest(Long userId, Long studyGroupId);

    List<JoinRequest> findJoinRequestsByGroup(Long studyGroupId);

    Optional<JoinRequest> findJoinRequestByUserAndGroup(Long userId, Long studyGroupId);

    boolean isMember(Long userId, Long studyGroupId);

    void addMember(Long userId, Long studyGroupId);

    void removeMember(Long userId, Long studyGroupId);
}
