package com.campusflow.application.audit.service;

import com.campusflow.application.audit.dto.ReportContentInput;
import com.campusflow.application.audit.dto.ReportContentResult;
import com.campusflow.application.audit.usecase.ReportContentUseCase;
import com.campusflow.domain.audit.model.AuditLogEntry;
import com.campusflow.domain.audit.port.AuditLogRepositoryPort;
import java.time.LocalDateTime;

public class ReportContentService implements ReportContentUseCase {
    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public ReportContentService(AuditLogRepositoryPort auditLogRepositoryPort) {
        this.auditLogRepositoryPort = auditLogRepositoryPort;
    }

    @Override
    public ReportContentResult report(ReportContentInput input) {
        AuditLogEntry saved = auditLogRepositoryPort.save(AuditLogEntry.builder()
                .id(null)
                .actorUserId(input.getActorUserId())
                .action("REPORT:" + input.getReason())
                .targetType(input.getTargetType())
                .targetId(input.getTargetId())
                .detail(input.getDetail())
                .createdAt(LocalDateTime.now())
                .build());

        return new ReportContentResult(
                saved.getId(),
                saved.getActorUserId(),
                saved.getTargetType(),
                saved.getTargetId(),
                input.getReason(),
                saved.getDetail(),
                saved.getCreatedAt()
        );
    }
}

