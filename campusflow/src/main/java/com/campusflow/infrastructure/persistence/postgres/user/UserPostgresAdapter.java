package com.campusflow.infrastructure.persistence.postgres.user;

import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPostgresAdapter implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity saved = userJpaRepository.save(UserJpaEntity.fromDomain(user));
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.findByEmail(email).isPresent();
    }
}
