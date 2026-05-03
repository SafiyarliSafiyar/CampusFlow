package com.campusflow.application.user.dto;

import lombok.Value;

@Value
public class RegisterUserInput {
    String username;
    String email;
    String rawPassword;
}
