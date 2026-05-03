package com.campusflow.application.push.service;

import com.campusflow.application.push.usecase.SendWebPushNotificationUseCase;
import com.campusflow.application.push.usecase.SendTestWebPushUseCase;
import com.campusflow.domain.user.model.PushToken;
import com.campusflow.domain.user.port.PushTokenRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.Map;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class WebPushService implements SendTestWebPushUseCase, SendWebPushNotificationUseCase {
    private final PushTokenRepositoryPort pushTokenRepositoryPort;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    public WebPushService(
            PushTokenRepositoryPort pushTokenRepositoryPort,
            ObjectMapper objectMapper,
            String subject,
            String publicKey,
            String privateKey
    ) {
        this.pushTokenRepositoryPort = pushTokenRepositoryPort;
        this.objectMapper = objectMapper;
        try {
            ensureBouncyCastle();
            if (isBlank(publicKey) || isBlank(privateKey)) {
                this.pushService = null;
            } else {
                this.pushService = new PushService(publicKey, privateKey, subject);
            }
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Invalid VAPID keys", e);
        }
    }

    @Override
    public void sendTest(Long userId, String message) {
        sendToUser(
                userId,
                "CampusFlow",
                message == null ? "Test notification from CampusFlow" : message,
                Map.of("type", "TEST", "url", "/")
        );
    }

    @Override
    public void sendToUser(Long userId, String title, String body, Map<String, Object> data) {
        if (userId == null || pushService == null) {
            return;
        }

        List<PushToken> tokens = pushTokenRepositoryPort.findByUserId(userId);
        if (tokens.isEmpty()) {
            return;
        }

        byte[] payload = toPayloadBytes(title, body, data);

        for (PushToken token : tokens) {
            if (!"WEB_PUSH".equalsIgnoreCase(token.getPlatform())) {
                continue;
            }
            Subscription subscription = parseSubscription(token.getToken());
            if (subscription == null) {
                continue;
            }
            try {
                Notification notification = new Notification(
                        subscription.endpoint,
                        subscription.keys.p256dh,
                        subscription.keys.auth,
                        payload
                );
                HttpResponse response = pushService.send(notification);
                int status = response.getStatusLine().getStatusCode();
                if (status == 404 || status == 410) {
                    pushTokenRepositoryPort.deleteByToken(token.getToken());
                }
            } catch (Exception _e) {
                // ignore individual failures
            }
        }
    }

    private byte[] toPayloadBytes(String title, String body, Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "title", title == null || title.isBlank() ? "CampusFlow" : title,
                    "body", body == null || body.isBlank() ? "You have a new notification." : body,
                    "data", data == null ? Map.of() : data
            ));
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception _e) {
            String fallback = body == null ? "" : body;
            return fallback.getBytes(StandardCharsets.UTF_8);
        }
    }

    private Subscription parseSubscription(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String endpoint = node.path("endpoint").asText(null);
            String p256dh = node.path("keys").path("p256dh").asText(null);
            String auth = node.path("keys").path("auth").asText(null);
            if (endpoint == null || p256dh == null || auth == null) {
                return null;
            }
            Subscription subscription = new Subscription();
            subscription.endpoint = endpoint;
            subscription.keys = new Subscription.Keys(p256dh, auth);
            return subscription;
        } catch (Exception e) {
            return null;
        }
    }

    private static void ensureBouncyCastle() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
