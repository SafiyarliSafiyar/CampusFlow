package com.campusflow.domain.user.port;

import com.campusflow.domain.user.model.User;

public interface TokenProviderPort {
    String generateToken(User user);

    String extractEmail(String token);

    Long extractUserId(String token);

    boolean isTokenValid(String token);
}
