package com.campusflow.domain.message.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadReceipt {
    private final String id;
    private final Long studyGroupId;
    private final String messageId;
    private final Long userId;
    private final LocalDateTime readAt;
}

