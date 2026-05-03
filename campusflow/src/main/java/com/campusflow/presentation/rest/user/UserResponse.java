package com.campusflow.presentation.rest.user;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class UserResponse {
    Long id;
    String username;
    String email;
    String major;
    String interests;
    String profilePhotoUrl;
    String visibility;
    String role;
    LocalDateTime createdAt;
}
