package com.campusflow.application.push.service;

import com.campusflow.application.push.usecase.SendTestWebPushUseCase;
import com.campusflow.domain.user.model.PushToken;
import com.campusflow.domain.user.port.PushTokenRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class WebPushService implements SendTestWebPushUseCase {
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
            this.pushService = new PushService(publicKey, privateKey, subject);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Invalid VAPID keys", e);
        }
    }

    @Override
    public void sendTest(Long userId, String message) {
        List<PushToken> tokens = pushTokenRepositoryPort.findByUserId(userId);
        if (tokens.isEmpty()) {
            return;
        }

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
                        message.getBytes(StandardCharsets.UTF_8)
                );
                HttpResponse response = pushService.send(notification);
                // best-effort: if gone/unauthorized, delete subscription
                int status = response.getStatusLine().getStatusCode();
                if (status == 404 || status == 410) {
                    pushTokenRepositoryPort.deleteByToken(token.getToken());
                }
            } catch (Exception e) {
                // ignore individual failures
            }
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
}

