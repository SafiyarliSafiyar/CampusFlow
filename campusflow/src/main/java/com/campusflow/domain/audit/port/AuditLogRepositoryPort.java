package com.campusflow.domain.audit.port;

import com.campusflow.domain.audit.model.AuditLogEntry;
import java.util.List;

public interface AuditLogRepositoryPort {
    AuditLogEntry save(AuditLogEntry entry);

    List<AuditLogEntry> findRecent(int limit);

    List<AuditLogEntry> findReports(int limit);
}
