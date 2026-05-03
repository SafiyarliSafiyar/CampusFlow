package com.campusflow.presentation.rest.notification;

import com.campusflow.application.notification.usecase.GetNotificationsUseCase;
import com.campusflow.application.notification.usecase.MarkNotificationsReadUseCase;
import com.campusflow.domain.notification.model.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final GetNotificationsUseCase getNotificationsUseCase;
    private final MarkNotificationsReadUseCase markNotificationsReadUseCase;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getLatest(@RequestParam(defaultValue = "50") int limit) {
        Long userId = extractUserId();
        List<Notification> notifications = getNotificationsUseCase.getLatest(userId, Math.min(Math.max(limit, 1), 200));
        List<NotificationResponse> response = notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getTitle(),
                        notification.getDetail(),
                        notification.getTone(),
                        notification.isRead(),
                        notification.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        Long userId = extractUserId();
        markNotificationsReadUseCase.markAllRead(userId);
        return ResponseEntity.ok().build();
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

