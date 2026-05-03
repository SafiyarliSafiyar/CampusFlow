package com.campusflow.application.audit.service;

import com.campusflow.application.audit.usecase.GetAuditLogUseCase;
import com.campusflow.domain.audit.model.AuditLogEntry;
import com.campusflow.domain.audit.port.AuditLogRepositoryPort;
import java.util.List;

public class GetAuditLogService implements GetAuditLogUseCase {
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public GetAuditLogService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    @Override
    public List<AuditLogEntry> getRecent(int limit) {
        return auditLogRepositoryPort.findRecent(limit);
    }
}

