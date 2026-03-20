package com.campusflow.application.studygroup.usecase;

import com.campusflow.application.studygroup.dto.JoinRequestResult;
import java.util.List;

public interface GetJoinRequestsUseCase {
    List<JoinRequestResult> getRequestsForGroup(Long studyGroupId, Long actorId);

    JoinRequestResult getMyRequestStatus(Long userId, Long studyGroupId);
}
