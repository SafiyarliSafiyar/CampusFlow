package com.campusflow.presentation.rest.push;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PushSubscriptionKeysRequest {
    @NotBlank
    private String p256dh;

    @NotBlank
    private String auth;
}

