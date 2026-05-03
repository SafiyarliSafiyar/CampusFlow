package com.campusflow.infrastructure.persistence.postgres.pushtoken;

import com.campusflow.domain.user.model.PushToken;
import com.campusflow.domain.user.port.PushTokenRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PushTokenPostgresAdapter implements PushTokenRepositoryPort {
    private final PushTokenJpaRepository pushTokenJpaRepository;

    @Override
    public PushToken save(PushToken pushToken) {
        PushTokenEntity saved = pushTokenJpaRepository.save(PushTokenEntity.fromDomain(pushToken));
        return saved.toDomain();
    }

    @Override
    public List<PushToken> findByUserId(Long userId) {
        return pushTokenJpaRepository.findByUserId(userId).stream().map(PushTokenEntity::toDomain).toList();
    }

    @Override
    public Optional<PushToken> findByToken(String token) {
        return pushTokenJpaRepository.findByToken(token).map(PushTokenEntity::toDomain);
    }

    @Override
    public void deleteByToken(String token) {
        pushTokenJpaRepository.deleteByToken(token);
    }
}

