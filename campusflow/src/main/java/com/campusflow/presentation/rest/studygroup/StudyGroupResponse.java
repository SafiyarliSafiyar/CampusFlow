package com.campusflow.presentation.rest.studygroup;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class StudyGroupResponse {
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
