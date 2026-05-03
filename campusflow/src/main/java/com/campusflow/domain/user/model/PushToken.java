package com.campusflow.domain.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PushToken {
    private final Long id;
    private final Long userId;
    private final String token;
    private final String platform;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastSeenAt;
}

