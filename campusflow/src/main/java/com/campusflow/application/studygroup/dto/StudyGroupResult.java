package com.campusflow.application.studygroup.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class StudyGroupResult {
    Long id;
    String name;
    String topic;
    String course;
    int capacity;
    int memberCount;
    Long creatorId;
    String status;
    LocalDateTime createdAt;
}
