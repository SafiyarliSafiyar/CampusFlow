package com.campusflow.presentation.rest.moderation;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class ReportContentResponse {
    Long id;
    Long actorUserId;
    String targetType;
    String targetId;
    String reason;
    String detail;
    LocalDateTime createdAt;
}

