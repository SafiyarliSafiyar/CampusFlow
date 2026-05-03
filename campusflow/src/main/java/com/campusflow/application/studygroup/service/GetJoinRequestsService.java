package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.JoinRequestResult;
import com.campusflow.application.studygroup.usecase.GetJoinRequestsUseCase;
import com.campusflow.domain.studygroup.exception.JoinRequestNotFoundException;
import com.campusflow.domain.studygroup.exception.NotGroupCreatorException;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.util.List;

public class GetJoinRequestsService implements GetJoinRequestsUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public GetJoinRequestsService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public List<JoinRequestResult> getRequestsForGroup(Long studyGroupId, Long actorId) {
        StudyGroup group = studyGroupRepositoryPort.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));
        if (!actorId.equals(group.getCreatorId())) {
            throw new NotGroupCreatorException();
        }

        return studyGroupRepositoryPort.findJoinRequestsByGroup(studyGroupId)
                .stream()
                .map(JoinRequestResultMapper::toResult)
                .toList();
    }

    @Override
    public JoinRequestResult getMyRequestStatus(Long userId, Long studyGroupId) {
        return studyGroupRepositoryPort.findJoinRequestByUserAndGroup(userId, studyGroupId)
                .map(JoinRequestResultMapper::toResult)
                .orElseThrow(() -> new JoinRequestNotFoundException(0L));
    }
}
