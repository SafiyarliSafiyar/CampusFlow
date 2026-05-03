package com.campusflow.infrastructure.persistence.postgres.studygroup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "study_group_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"studyGroupId", "userId"})
)
@Getter
@Setter
@NoArgsConstructor
public class StudyGroupMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyGroupId;

    @Column(nullable = false)
    private Long userId;
}
