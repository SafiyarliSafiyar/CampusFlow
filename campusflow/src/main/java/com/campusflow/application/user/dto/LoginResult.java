package com.campusflow.application.user.dto;

import lombok.Value;

@Value
public class LoginResult {
    String token;
    Long userId;
    String username;
    String email;
    String role;
}
