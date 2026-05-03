package com.campusflow.infrastructure.websocket;

import com.campusflow.domain.message.port.MessagePushPort;
import com.campusflow.domain.message.port.ReadReceiptRepositoryPort;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class StudyGroupWebSocketConfig implements WebSocketConfigurer {
    private final TokenProviderPort tokenProviderPort;
    private final UserRepositoryPort userRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;
    private final StudyGroupWebSocketRegistry registry;
    private final ObjectMapper objectMapper;
    private final MessagePushPort messagePushPort;
    private final ReadReceiptRepositoryPort readReceiptRepositoryPort;

    public StudyGroupWebSocketConfig(
            TokenProviderPort tokenProviderPort,
            UserRepositoryPort userRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            StudyGroupWebSocketRegistry registry,
            ObjectMapper objectMapper,
            MessagePushPort messagePushPort,
            ReadReceiptRepositoryPort readReceiptRepositoryPort
    ) {
        this.tokenProviderPort = tokenProviderPort;
        this.userRepositoryPort = userRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
        this.registry = registry;
        this.objectMapper = objectMapper;
        this.messagePushPort = messagePushPort;
        this.readReceiptRepositoryPort = readReceiptRepositoryPort;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                        new StudyGroupWebSocketHandler(
                                this.registry,
                                objectMapper,
                                messagePushPort,
                                readReceiptRepositoryPort
                        ),
                        "/ws/study-groups/{groupId}"
                )
                .addInterceptors(new StudyGroupWebSocketHandshakeInterceptor(
                        tokenProviderPort,
                        userRepositoryPort,
                        studyGroupRepositoryPort
                ))
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                );
    }
}

