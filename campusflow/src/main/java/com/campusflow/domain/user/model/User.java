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
    private final String major;
    private final String interests;
    private final String profilePhotoUrl;
    @Builder.Default
    private final ProfileVisibility visibility = ProfileVisibility.CAMPUS_ONLY;
    private final String passwordHash;
    private final UserRole role;
    @Builder.Default
    private final VerificationStatus verificationStatus = VerificationStatus.PENDING;
    private final LocalDateTime createdAt;
}
