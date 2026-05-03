package com.campusflow.application.audit.usecase;

import com.campusflow.domain.audit.model.AuditLogEntry;
import java.util.List;

public interface GetModerationQueueUseCase {
    List<AuditLogEntry> getRecentReports(int limit);
}

