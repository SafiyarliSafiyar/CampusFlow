package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.JoinRequestResult;
import com.campusflow.application.studygroup.usecase.RequestJoinUseCase;
import com.campusflow.domain.studygroup.exception.AlreadyMemberException;
import com.campusflow.domain.studygroup.exception.AlreadyRequestedException;
import com.campusflow.domain.studygroup.exception.StudyGroupFullException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.time.LocalDateTime;

public class RequestJoinService implements RequestJoinUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public RequestJoinService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public JoinRequestResult requestJoin(Long userId, Long studyGroupId) {
        StudyGroup group = studyGroupRepositoryPort.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));

        if (!group.isOpen()) {
            throw new StudyGroupFullException();
        }
        if (!group.hasCapacity()) {
            throw new StudyGroupFullException();
        }
        if (studyGroupRepositoryPort.isMember(userId, studyGroupId)) {
            throw new AlreadyMemberException();
        }
        if (studyGroupRepositoryPort.findPendingRequest(userId, studyGroupId).isPresent()) {
            throw new AlreadyRequestedException();
        }

        JoinRequest request = JoinRequest.builder()
                .id(null)
                .studyGroupId(studyGroupId)
                .userId(userId)
                .status(JoinRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        JoinRequest saved = studyGroupRepositoryPort.saveJoinRequest(request);
        return JoinRequestResultMapper.toResult(saved);
    }
}
