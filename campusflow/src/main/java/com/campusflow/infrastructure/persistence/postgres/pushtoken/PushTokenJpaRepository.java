package com.campusflow.infrastructure.persistence.postgres.pushtoken;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenJpaRepository extends JpaRepository<PushTokenEntity, Long> {
    List<PushTokenEntity> findByUserId(Long userId);

    Optional<PushTokenEntity> findByToken(String token);

    void deleteByToken(String token);
}

