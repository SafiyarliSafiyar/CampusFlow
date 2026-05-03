package com.campusflow.application.studygroup.service;

import com.campusflow.application.studygroup.dto.StudyGroupResult;
import com.campusflow.application.studygroup.usecase.GetStudyGroupsUseCase;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.util.List;

public class GetStudyGroupsService implements GetStudyGroupsUseCase {
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public GetStudyGroupsService(StudyGroupRepositoryPort studyGroupRepositoryPort) {
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public StudyGroupResult getById(Long id) {
        return studyGroupRepositoryPort.findById(id)
                .map(StudyGroupResultMapper::toResult)
                .orElseThrow(() -> new StudyGroupNotFoundException(id));
    }

    @Override
    public List<StudyGroupResult> getAllOpen() {
        return studyGroupRepositoryPort.findAllOpen()
                .stream()
                .map(StudyGroupResultMapper::toResult)
                .toList();
    }

    @Override
    public List<StudyGroupResult> search(String keyword) {
        return studyGroupRepositoryPort.searchByKeyword(keyword)
                .stream()
                .map(StudyGroupResultMapper::toResult)
                .toList();
    }
}
