package com.campusflow.infrastructure.persistence.postgres.studygroup;

import com.campusflow.domain.studygroup.model.JoinRequest;
import com.campusflow.domain.studygroup.model.JoinRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "join_requests")
@Getter
@Setter
@NoArgsConstructor
public class JoinRequestJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyGroupId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    public JoinRequest toDomain() {
        return JoinRequest.builder()
                .id(id)
                .studyGroupId(studyGroupId)
                .userId(userId)
                .status(status)
                .requestedAt(requestedAt)
                .build();
    }

    public static JoinRequestJpaEntity fromDomain(JoinRequest request) {
        JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
        entity.setId(request.getId());
        entity.setStudyGroupId(request.getStudyGroupId());
        entity.setUserId(request.getUserId());
        entity.setStatus(request.getStatus());
        entity.setRequestedAt(request.getRequestedAt());
        return entity;
    }
}
