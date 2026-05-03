package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.StudyGroupResult;
import com.campusflow.domain.studygroup.model.StudyGroup;

final class StudyGroupResultMapper {
    private StudyGroupResultMapper() {
    }

    static StudyGroupResult toResult(StudyGroup group) {
        return new StudyGroupResult(
                group.getId(),
                group.getName(),
                group.getTopic(),
                group.getCourse(),
                group.getCapacity(),
                group.getMemberCount(),
                group.getCreatorId(),
                group.getStatus().name(),
                group.getCreatedAt()
        );
    }
}
