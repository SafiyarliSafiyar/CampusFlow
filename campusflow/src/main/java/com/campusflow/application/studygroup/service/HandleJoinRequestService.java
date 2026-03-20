package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.JoinRequestResult;
import com.campusflow.application.studygroup.usecase.HandleJoinRequestUseCase;
import com.campusflow.domain.studygroup.exception.JoinRequestNotFoundException;
import com.campusflow.domain.studygroup.exception.NotGroupCreatorException;
import com.campusflow.domain.studygroup.exception.StudyGroupFullException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;

public class HandleJoinRequestService implements HandleJoinRequestUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public HandleJoinRequestService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public JoinRequestResult approve(Long requestId, Long actorId) {
        JoinRequest request = studyGroupRepositoryPort.findJoinRequestById(requestId)
                .orElseThrow(() -> new JoinRequestNotFoundException(requestId));
        StudyGroup group = studyGroupRepositoryPort.findById(request.getStudyGroupId())
                .orElseThrow(() -> new StudyGroupNotFoundException(request.getStudyGroupId()));

        requireCreator(actorId, group);
        if (!group.hasCapacity()) {
            throw new StudyGroupFullException();
        }

        JoinRequest updatedRequest = JoinRequest.builder()
                .id(request.getId())
                .studyGroupId(request.getStudyGroupId())
                .userId(request.getUserId())
                .status(JoinRequestStatus.ACCEPTED)
                .requestedAt(request.getRequestedAt())
                .build();
        JoinRequest savedRequest = studyGroupRepositoryPort.saveJoinRequest(updatedRequest);

        studyGroupRepositoryPort.addMember(request.getUserId(), group.getId());
        StudyGroup updatedGroup = StudyGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .topic(group.getTopic())
                .course(group.getCourse())
                .capacity(group.getCapacity())
                .memberCount(group.getMemberCount() + 1)
                .creatorId(group.getCreatorId())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .build();
        studyGroupRepositoryPort.save(updatedGroup);

        return JoinRequestResultMapper.toResult(savedRequest);
    }

    @Override
    public JoinRequestResult reject(Long requestId, Long actorId) {
        JoinRequest request = studyGroupRepositoryPort.findJoinRequestById(requestId)
                .orElseThrow(() -> new JoinRequestNotFoundException(requestId));
        StudyGroup group = studyGroupRepositoryPort.findById(request.getStudyGroupId())
                .orElseThrow(() -> new StudyGroupNotFoundException(request.getStudyGroupId()));

        requireCreator(actorId, group);

        JoinRequest updatedRequest = JoinRequest.builder()
                .id(request.getId())
                .studyGroupId(request.getStudyGroupId())
                .userId(request.getUserId())
                .status(JoinRequestStatus.REJECTED)
                .requestedAt(request.getRequestedAt())
                .build();
        JoinRequest savedRequest = studyGroupRepositoryPort.saveJoinRequest(updatedRequest);
        return JoinRequestResultMapper.toResult(savedRequest);
    }

    private void requireCreator(Long actorId, StudyGroup group) {
        if (!actorId.equals(group.getCreatorId())) {
            throw new NotGroupCreatorException();
        }
    }
}
