package com.campusflow.application.audit.usecase;

import com.campusflow.domain.audit.model.AuditLogEntry;
import java.util.List;

public interface GetAuditLogUseCase {
    List<AuditLogEntry> getRecent(int limit);
}

