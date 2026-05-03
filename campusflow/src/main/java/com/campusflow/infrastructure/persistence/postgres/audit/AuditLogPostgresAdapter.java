package com.campusflow.infrastructure.persistence.postgres.audit;

import com.campusflow.domain.audit.model.AuditLogEntry;
import com.campusflow.domain.audit.port.AuditLogRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogPostgresAdapter implements AuditLogRepositoryPort {
    private final AuditLogJpaRepository auditLogJpaRepository;

    @Override
    public AuditLogEntry save(AuditLogEntry entry) {
        AuditLogEntryEntity saved = auditLogJpaRepository.save(AuditLogEntryEntity.fromDomain(entry));
        return saved.toDomain();
    }

    @Override
    public List<AuditLogEntry> findRecent(int limit) {
        return auditLogJpaRepository.findRecent(PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(AuditLogEntryEntity::toDomain)
                .toList();
    }

    @Override
    public List<AuditLogEntry> findReports(int limit) {
        return auditLogJpaRepository.findReports(PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(AuditLogEntryEntity::toDomain)
                .toList();
    }
}
