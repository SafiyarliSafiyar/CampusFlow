package com.campusflow.presentation.rest.push;

import com.campusflow.application.push.usecase.RegisterWebPushSubscriptionUseCase;
import com.campusflow.application.push.usecase.SendTestWebPushUseCase;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/push")
public class PushController {
    private final String vapidPublicKey;
    private final RegisterWebPushSubscriptionUseCase registerWebPushSubscriptionUseCase;
    private final SendTestWebPushUseCase sendTestWebPushUseCase;

    public PushController(
            @Value("${campusflow.webpush.public-key:}") String vapidPublicKey,
            RegisterWebPushSubscriptionUseCase registerWebPushSubscriptionUseCase,
            SendTestWebPushUseCase sendTestWebPushUseCase
    ) {
        this.vapidPublicKey = vapidPublicKey;
        this.registerWebPushSubscriptionUseCase = registerWebPushSubscriptionUseCase;
        this.sendTestWebPushUseCase = sendTestWebPushUseCase;
    }

    @GetMapping("/vapid-public-key")
    public ResponseEntity<WebPushPublicKeyResponse> getPublicKey() {
        return ResponseEntity.ok(new WebPushPublicKeyResponse(vapidPublicKey));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody @Valid PushSubscriptionRequest request) {
        registerWebPushSubscriptionUseCase.register(extractUserId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> test(@RequestBody(required = false) Map<String, String> body) {
        String message = body == null ? "Test notification from CampusFlow" : body.getOrDefault("message", "Test notification from CampusFlow");
        sendTestWebPushUseCase.sendTest(extractUserId(), message);
        return ResponseEntity.ok(Map.of("message", "Push sent (if subscribed)."));
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException(
                "User id is missing from authentication context");
    }
}
