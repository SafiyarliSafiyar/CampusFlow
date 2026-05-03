package com.campusflow.presentation.rest.push;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PushSubscriptionRequest {
    @NotBlank
    @Size(max = 2048)
    private String endpoint;

    @NotNull
    @Valid
    private PushSubscriptionKeysRequest keys;
}

