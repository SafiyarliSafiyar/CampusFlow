package com.campusflow.infrastructure.persistence.postgres.studygroup;

import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyGroupPostgresAdapter implements StudyGroupRepositoryPort {
    private final StudyGroupJpaRepository studyGroupJpaRepository;
    private final JoinRequestJpaRepository joinRequestJpaRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;

    @Override
    public StudyGroup save(StudyGroup studyGroup) {
        StudyGroupJpaEntity saved = studyGroupJpaRepository.save(StudyGroupJpaEntity.fromDomain(studyGroup));
        return saved.toDomain();
    }

    @Override
    public Optional<StudyGroup> findById(Long id) {
        return studyGroupJpaRepository.findById(id).map(StudyGroupJpaEntity::toDomain);
    }

    @Override
    public List<StudyGroup> findAllOpen() {
        return studyGroupJpaRepository.findAllOpen()
                .stream()
                .map(StudyGroupJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<StudyGroup> searchByKeyword(String keyword) {
        return studyGroupJpaRepository.searchByKeyword(keyword)
                .stream()
                .map(StudyGroupJpaEntity::toDomain)
                .toList();
    }

    @Override
    public JoinRequest saveJoinRequest(JoinRequest joinRequest) {
        JoinRequestJpaEntity saved = joinRequestJpaRepository.save(JoinRequestJpaEntity.fromDomain(joinRequest));
        return saved.toDomain();
    }

    @Override
    public Optional<JoinRequest> findJoinRequestById(Long requestId) {
        return joinRequestJpaRepository.findById(requestId).map(JoinRequestJpaEntity::toDomain);
    }

    @Override
    public Optional<JoinRequest> findPendingRequest(Long userId, Long studyGroupId) {
        return joinRequestJpaRepository
                .findByUserIdAndStudyGroupIdAndStatus(userId, studyGroupId, JoinRequestStatus.PENDING)
                .map(JoinRequestJpaEntity::toDomain);
    }

    @Override
    public List<JoinRequest> findJoinRequestsByGroup(Long studyGroupId) {
        return joinRequestJpaRepository.findByStudyGroupId(studyGroupId)
                .stream()
                .map(JoinRequestJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<JoinRequest> findJoinRequestByUserAndGroup(Long userId, Long studyGroupId) {
        return joinRequestJpaRepository.findByUserIdAndStudyGroupId(userId, studyGroupId)
                .map(JoinRequestJpaEntity::toDomain);
    }

    @Override
    public boolean isMember(Long userId, Long studyGroupId) {
        return studyGroupMemberRepository.existsByStudyGroupIdAndUserId(studyGroupId, userId);
    }

    @Override
    public void addMember(Long userId, Long studyGroupId) {
        StudyGroupMemberEntity memberEntity = new StudyGroupMemberEntity();
        memberEntity.setStudyGroupId(studyGroupId);
        memberEntity.setUserId(userId);
        studyGroupMemberRepository.save(memberEntity);
    }

    @Override
    public void removeMember(Long userId, Long studyGroupId) {
        studyGroupMemberRepository.deleteByStudyGroupIdAndUserId(studyGroupId, userId);
    }
}
