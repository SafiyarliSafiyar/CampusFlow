package com.campusflow.domain.studygroup.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JoinRequest {
    private final Long id;
    private final Long studyGroupId;
    private final Long userId;
    private final JoinRequestStatus status;
    private final LocalDateTime requestedAt;
}
