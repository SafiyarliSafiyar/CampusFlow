package com.campusflow.domain.audit.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuditLogEntry {
    private final Long id;
    private final Long actorUserId;
    private final String action;
    private final String targetType;
    private final String targetId;
    private final String detail;
    private final LocalDateTime createdAt;
}

