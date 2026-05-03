package com.campusflow.infrastructure.persistence.postgres.audit;

import com.campusflow.domain.audit.model.AuditLogEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_log_entries")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long actorUserId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private String targetId;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AuditLogEntry toDomain() {
        return AuditLogEntry.builder()
                .id(id)
                .actorUserId(actorUserId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .detail(detail)
                .createdAt(createdAt)
                .build();
    }

    public static AuditLogEntryEntity fromDomain(AuditLogEntry entry) {
        AuditLogEntryEntity entity = new AuditLogEntryEntity();
        entity.setId(entry.getId());
        entity.setActorUserId(entry.getActorUserId());
        entity.setAction(entry.getAction());
        entity.setTargetType(entry.getTargetType());
        entity.setTargetId(entry.getTargetId());
        entity.setDetail(entry.getDetail());
        entity.setCreatedAt(entry.getCreatedAt());
        return entity;
    }
}

