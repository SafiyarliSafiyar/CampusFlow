package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.JoinRequestResult;
import com.campusflow.domain.studygroup.model.JoinRequest;

final class JoinRequestResultMapper {
    private JoinRequestResultMapper() {
    }

    static JoinRequestResult toResult(JoinRequest request) {
        return new JoinRequestResult(
                request.getId(),
                request.getStudyGroupId(),
                request.getUserId(),
                request.getStatus().name(),
                request.getRequestedAt()
        );
    }
}
