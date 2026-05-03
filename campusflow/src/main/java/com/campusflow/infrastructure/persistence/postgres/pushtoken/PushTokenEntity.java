package com.campusflow.infrastructure.persistence.postgres.pushtoken;

import com.campusflow.domain.user.model.PushToken;
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
@Table(name = "push_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PushTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @Column
    private String platform;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastSeenAt;

    public PushToken toDomain() {
        return PushToken.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .platform(platform)
                .createdAt(createdAt)
                .lastSeenAt(lastSeenAt)
                .build();
    }

    public static PushTokenEntity fromDomain(PushToken pushToken) {
        PushTokenEntity entity = new PushTokenEntity();
        entity.setId(pushToken.getId());
        entity.setUserId(pushToken.getUserId());
        entity.setToken(pushToken.getToken());
        entity.setPlatform(pushToken.getPlatform());
        entity.setCreatedAt(pushToken.getCreatedAt());
        entity.setLastSeenAt(pushToken.getLastSeenAt());
        return entity;
    }
}
