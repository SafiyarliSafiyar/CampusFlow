package com.campusflow.application.studygroup.usecase;

import com.campusflow.application.studygroup.dto.JoinRequestResult;

public interface HandleJoinRequestUseCase {
    JoinRequestResult approve(Long requestId, Long actorId);

    JoinRequestResult reject(Long requestId, Long actorId);
}
