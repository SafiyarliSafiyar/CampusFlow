package com.campusflow.application.audit.usecase;

import com.campusflow.application.audit.dto.ReportContentInput;
import com.campusflow.application.audit.dto.ReportContentResult;

public interface ReportContentUseCase {
    ReportContentResult report(ReportContentInput input);
}

