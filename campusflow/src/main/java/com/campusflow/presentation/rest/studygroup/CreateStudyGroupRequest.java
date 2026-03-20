package com.campusflow.presentation.rest.studygroup;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStudyGroupRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String topic;

    @NotBlank
    private String course;

    @Min(2)
    @Max(50)
    private int capacity;
}
