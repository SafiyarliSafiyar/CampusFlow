package com.campusflow.domain.user.port;

import com.campusflow.domain.user.model.PushToken;
import java.util.List;
import java.util.Optional;

public interface PushTokenRepositoryPort {
    PushToken save(PushToken pushToken);

    List<PushToken> findByUserId(Long userId);

    Optional<PushToken> findByToken(String token);

    void deleteByToken(String token);
}

