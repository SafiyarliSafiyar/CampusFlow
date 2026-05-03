package com.campusflow.application.audit.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class ReportContentResult {
    Long id;
    Long actorUserId;
    String targetType;
    String targetId;
    String reason;
    String detail;
    LocalDateTime createdAt;
}

