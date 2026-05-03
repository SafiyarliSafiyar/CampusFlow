package com.campusflow.presentation.rest.moderation;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class AuditLogEntryResponse {
    Long id;
    Long actorUserId;
    String action;
    String targetType;
    String targetId;
    String detail;
    LocalDateTime createdAt;
}

