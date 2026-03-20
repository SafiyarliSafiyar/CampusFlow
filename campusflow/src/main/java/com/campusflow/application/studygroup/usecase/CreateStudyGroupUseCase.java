package com.campusflow.application.studygroup.usecase;

import com.campusflow.application.studygroup.dto.CreateStudyGroupInput;
import com.campusflow.application.studygroup.dto.StudyGroupResult;

public interface CreateStudyGroupUseCase {
    StudyGroupResult create(CreateStudyGroupInput input);
}
