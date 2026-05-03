package com.campusflow.application.notification.service;

import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.application.push.usecase.SendWebPushNotificationUseCase;
import com.campusflow.domain.notification.model.Notification;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;
import java.time.LocalDateTime;
import java.util.Map;

public class NotificationOrchestratorService implements NotificationOrchestratorUseCase {
    private final NotificationRepositoryPort notificationRepositoryPort;
    private final SendWebPushNotificationUseCase sendWebPushNotificationUseCase;

    public NotificationOrchestratorService(
            NotificationRepositoryPort notificationRepositoryPort,
            SendWebPushNotificationUseCase sendWebPushNotificationUseCase
    ) {
        this.notificationRepositoryPort = notificationRepositoryPort;
        this.sendWebPushNotificationUseCase = sendWebPushNotificationUseCase;
    }

    @Override
    public void notifyJoinRequestDecision(Long targetUserId, Long groupId, boolean approved) {
        String title = approved ? "Join request approved" : "Join request rejected";
        String detail = "Study group #" + groupId + " updated your join request.";

        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(targetUserId)
                .type("JOIN_REQUEST")
                .title(title)
                .detail(detail)
                .tone(approved ? "success" : "warning")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        sendWebPushNotificationUseCase.sendToUser(
                targetUserId,
                "CampusFlow",
                title + " · " + detail,
                Map.of("type", "JOIN_REQUEST", "groupId", groupId, "url", "/")
        );
    }

    @Override
    public void notifyRsvpConfirmation(Long targetUserId, Long eventId, String eventTitle) {
        String title = "RSVP confirmed";
        String detail = "You are going to " + eventTitle + " (event #" + eventId + ").";

        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(targetUserId)
                .type("RSVP")
                .title(title)
                .detail(detail)
                .tone("success")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        sendWebPushNotificationUseCase.sendToUser(
                targetUserId,
                "CampusFlow",
                title + " · " + detail,
                Map.of("type", "RSVP", "eventId", eventId, "url", "/")
        );
    }

    @Override
    public void notifyNewMessageToGroupCreator(Long creatorUserId, Long groupId, String senderUsername) {
        String title = "New group message";
        String detail = senderUsername + " posted a new message in study group #" + groupId + ".";

        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(creatorUserId)
                .type("MESSAGE")
                .title(title)
                .detail(detail)
                .tone("info")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());

        sendWebPushNotificationUseCase.sendToUser(
                creatorUserId,
                "CampusFlow",
                title + " · " + detail,
                Map.of("type", "MESSAGE", "groupId", groupId, "url", "/")
        );
    }
}
