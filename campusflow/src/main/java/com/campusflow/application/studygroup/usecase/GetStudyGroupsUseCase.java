package com.campusflow.application.studygroup.usecase;

import com.campusflow.application.studygroup.dto.StudyGroupResult;
import java.util.List;

public interface GetStudyGroupsUseCase {
    StudyGroupResult getById(Long id);

    List<StudyGroupResult> getAllOpen();

    List<StudyGroupResult> search(String keyword);
}
