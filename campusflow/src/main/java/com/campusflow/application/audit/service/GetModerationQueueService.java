package com.campusflow.application.audit.service;

import com.campusflow.application.audit.usecase.GetModerationQueueUseCase;
import com.campusflow.domain.audit.model.AuditLogEntry;
import com.campusflow.domain.audit.port.AuditLogRepositoryPort;
import java.util.List;

public class GetModerationQueueService implements GetModerationQueueUseCase {
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public GetModerationQueueService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    @Override
    public List<AuditLogEntry> getRecentReports(int limit) {
        return auditLogRepositoryPort.findReports(limit);
    }
}

