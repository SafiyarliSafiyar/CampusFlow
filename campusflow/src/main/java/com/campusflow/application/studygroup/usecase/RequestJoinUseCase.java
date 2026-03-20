package com.campusflow.application.studygroup.usecase;

import com.campusflow.application.studygroup.dto.JoinRequestResult;

public interface RequestJoinUseCase {
    JoinRequestResult requestJoin(Long userId, Long studyGroupId);
}
