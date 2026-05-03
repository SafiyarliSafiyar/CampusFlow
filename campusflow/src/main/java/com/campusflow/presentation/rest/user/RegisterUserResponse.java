package com.campusflow.presentation.rest.user;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class RegisterUserResponse {
    Long id;
    String username;
    String email;
    String role;
    LocalDateTime createdAt;
}
