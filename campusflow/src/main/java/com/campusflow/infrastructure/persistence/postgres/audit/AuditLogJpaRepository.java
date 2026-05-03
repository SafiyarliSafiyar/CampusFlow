package com.campusflow.infrastructure.persistence.postgres.audit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntryEntity, Long> {
    @Query("select a from AuditLogEntryEntity a order by a.createdAt desc")
    List<AuditLogEntryEntity> findRecent(org.springframework.data.domain.Pageable pageable);

    @Query("select a from AuditLogEntryEntity a where a.action like 'REPORT:%' order by a.createdAt desc")
    List<AuditLogEntryEntity> findReports(org.springframework.data.domain.Pageable pageable);
}
