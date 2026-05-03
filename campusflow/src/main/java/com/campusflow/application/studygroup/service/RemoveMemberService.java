package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.usecase.RemoveMemberUseCase;
import com.campusflow.domain.studygroup.exception.MemberNotFoundException;
import com.campusflow.domain.studygroup.exception.NotGroupCreatorException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;

public class RemoveMemberService implements RemoveMemberUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public RemoveMemberService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public void removeMember(Long studyGroupId, Long memberId, Long actorId) {
        StudyGroup group = studyGroupRepositoryPort.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        if (!actorId.equals(group.getCreatorId())) {
            throw new NotGroupCreatorException();
        }
        if (!studyGroupRepositoryPort.isMember(memberId, studyGroupId)) {
            throw new MemberNotFoundException();
        }

        studyGroupRepositoryPort.removeMember(memberId, studyGroupId);
        int nextMemberCount = Math.max(0, group.getMemberCount() - 1);
        StudyGroup updated = StudyGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .topic(group.getTopic())
                .course(group.getCourse())
                .capacity(group.getCapacity())
                .memberCount(nextMemberCount)
                .creatorId(group.getCreatorId())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .build();
        studyGroupRepositoryPort.save(updated);
    }
}
