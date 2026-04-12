package com.campusflow.infrastructure.persistence.postgres.user;

import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.model.UserRole;
import com.campusflow.domain.user.model.VerificationStatus;
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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public User toDomain() {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .verificationStatus(verificationStatus)
                .createdAt(createdAt)
                .build();
    }

    public static UserJpaEntity fromDomain(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole());
        entity.setVerificationStatus(user.getVerificationStatus());
        entity.setCreatedAt(user.getCreatedAt());
        return entity;
    }
}
