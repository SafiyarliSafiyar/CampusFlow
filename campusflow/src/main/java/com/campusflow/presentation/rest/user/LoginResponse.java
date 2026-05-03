package com.campusflow.presentation.rest.user;

import lombok.Value;

@Value
public class LoginResponse {
    String token;
    Long userId;
    String username;
    String email;
    String role;
}
