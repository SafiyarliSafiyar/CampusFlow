package com.campusflow.domain.notification.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Notification {
    private final String id;
    private final Long userId;
    private final String type;
    private final String title;
    private final String detail;
    private final String tone;
    private final boolean read;
    private final LocalDateTime createdAt;
}

