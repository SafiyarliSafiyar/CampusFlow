package com.campusflow.application.studygroup.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class JoinRequestResult {
    Long id;
    Long studyGroupId;
    Long userId;
    String status;
    LocalDateTime requestedAt;
}
