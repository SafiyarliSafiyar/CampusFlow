package com.campusflow.domain.studygroup.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyGroup {
    private final Long id;
    private final String name;
    private final String topic;
    private final String course;
    private final int capacity;
    private final int memberCount;
    private final Long creatorId;
    private final StudyGroupStatus status;
    private final LocalDateTime createdAt;

    public boolean hasCapacity() {
        return memberCount < capacity;
    }

    public boolean isOpen() {
        return status == StudyGroupStatus.OPEN;
    }
}
