package com.campusflow.presentation.rest.studygroup;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class JoinRequestResponse {
    Long id;
    Long studyGroupId;
    Long userId;
    String status;
    LocalDateTime requestedAt;
}
