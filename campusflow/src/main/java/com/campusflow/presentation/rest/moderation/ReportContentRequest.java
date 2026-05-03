package com.campusflow.presentation.rest.moderation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportContentRequest {
    @NotBlank
    @Pattern(regexp = "^(POST|EVENT|STUDY_GROUP|MESSAGE|USER)$", message = "targetType must be POST, EVENT, STUDY_GROUP, MESSAGE, or USER")
    private String targetType;

    @NotBlank
    @Size(max = 64)
    private String targetId;

    @NotBlank
    @Size(max = 40)
    private String reason;

    @Size(max = 1000)
    private String detail;
}

