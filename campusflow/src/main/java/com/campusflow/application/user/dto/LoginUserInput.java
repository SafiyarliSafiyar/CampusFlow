package com.campusflow.application.user.dto;

import lombok.Value;

@Value
public class LoginUserInput {
    String email;
    String rawPassword;
}
