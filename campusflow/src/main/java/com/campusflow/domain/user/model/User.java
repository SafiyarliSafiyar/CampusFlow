package com.campusflow.domain.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final LocalDateTime createdAt;
}
