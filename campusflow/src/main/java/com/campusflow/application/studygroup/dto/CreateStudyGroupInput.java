package com.campusflow.application.studygroup.dto;

import lombok.Value;

@Value
public class CreateStudyGroupInput {
    String name;
    String topic;
    String course;
    int capacity;
    Long creatorId;
}
