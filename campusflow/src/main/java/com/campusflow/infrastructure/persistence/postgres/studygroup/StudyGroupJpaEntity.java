package com.campusflow.infrastructure.persistence.postgres.studygroup;

import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.model.StudyGroupStatus;
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
@Table(name = "study_groups")
@Getter
@Setter
@NoArgsConstructor
public class StudyGroupJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int memberCount;

    @Column(nullable = false)
    private Long creatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyGroupStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public StudyGroup toDomain() {
        return StudyGroup.builder()
                .id(id)
                .name(name)
                .topic(topic)
                .course(course)
                .capacity(capacity)
                .memberCount(memberCount)
                .creatorId(creatorId)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

    public static StudyGroupJpaEntity fromDomain(StudyGroup group) {
        StudyGroupJpaEntity entity = new StudyGroupJpaEntity();
        entity.setId(group.getId());
        entity.setName(group.getName());
        entity.setTopic(group.getTopic());
        entity.setCourse(group.getCourse());
        entity.setCapacity(group.getCapacity());
        entity.setMemberCount(group.getMemberCount());
        entity.setCreatorId(group.getCreatorId());
        entity.setStatus(group.getStatus());
        entity.setCreatedAt(group.getCreatedAt());
        return entity;
    }
}
