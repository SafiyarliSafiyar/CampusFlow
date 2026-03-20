package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.CreateStudyGroupInput;
import com.campusflow.application.studygroup.dto.StudyGroupResult;
import com.campusflow.application.studygroup.usecase.CreateStudyGroupUseCase;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.model.StudyGroupStatus;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.time.LocalDateTime;

public class CreateStudyGroupService implements CreateStudyGroupUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public CreateStudyGroupService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public StudyGroupResult create(CreateStudyGroupInput input) {
        StudyGroup group = StudyGroup.builder()
                .id(null)
                .name(input.getName())
                .topic(input.getTopic())
                .course(input.getCourse())
                .capacity(input.getCapacity())
                .memberCount(1)
                .creatorId(input.getCreatorId())
                .status(StudyGroupStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        StudyGroup saved = studyGroupRepositoryPort.save(group);
        studyGroupRepositoryPort.addMember(input.getCreatorId(), saved.getId());
        return StudyGroupResultMapper.toResult(saved);
    }
}
