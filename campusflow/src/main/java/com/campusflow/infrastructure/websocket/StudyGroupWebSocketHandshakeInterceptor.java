package com.campusflow.infrastructure.websocket;

import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.model.User;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class StudyGroupWebSocketHandshakeInterceptor implements HandshakeInterceptor {
    public static final String ATTR_GROUP_ID = "campusflow.groupId";
    public static final String ATTR_USER_ID = "campusflow.userId";
    public static final String ATTR_USERNAME = "campusflow.username";

    private final TokenProviderPort tokenProviderPort;
    private final UserRepositoryPort userRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public StudyGroupWebSocketHandshakeInterceptor(
            TokenProviderPort tokenProviderPort,
            UserRepositoryPort userRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort
    ) {
        this.tokenProviderPort = tokenProviderPort;
        this.userRepositoryPort = userRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Long groupId = extractGroupId(request.getURI());
        if (groupId == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        String token = extractQueryParam(request.getURI(), "token");
        if (token == null || token.isBlank() || !tokenProviderPort.isTokenValid(token)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        Long userId = tokenProviderPort.extractUserId(token);
        Optional<User> userOpt = userRepositoryPort.findById(userId);
        if (userOpt.isEmpty()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        if (!studyGroupRepositoryPort.isMember(userId, groupId)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        attributes.put(ATTR_GROUP_ID, groupId);
        attributes.put(ATTR_USER_ID, userId);
        attributes.put(ATTR_USERNAME, userOpt.get().getUsername());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }

    private Long extractGroupId(URI uri) {
        // expected: /ws/study-groups/{groupId}
        String path = uri.getPath();
        if (path == null) {
            return null;
        }
        String[] parts = path.split("/");
        if (parts.length < 4) {
            return null;
        }
        String last = parts[parts.length - 1];
        try {
            return Long.parseLong(last);
        } catch (NumberFormatException _e) {
            return null;
        }
    }

    private String extractQueryParam(URI uri, String key) {
        String query = uri.getQuery();
        if (query == null || query.isBlank()) {
            return null;
        }
        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String k = pair.substring(0, idx);
            if (!k.equals(key)) {
                continue;
            }
            return pair.substring(idx + 1);
        }
        return null;
    }
}
