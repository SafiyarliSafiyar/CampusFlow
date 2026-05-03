package com.campusflow.application.audit.dto;

import lombok.Value;

@Value
public class ReportContentInput {
    Long actorUserId;
    String targetType;
    String targetId;
    String reason;
    String detail;
}

