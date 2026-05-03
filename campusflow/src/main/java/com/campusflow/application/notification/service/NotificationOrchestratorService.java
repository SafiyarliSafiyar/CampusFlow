package com.campusflow.application.notification.service;

import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.domain.notification.model.Notification;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;
import java.time.LocalDateTime;

public class NotificationOrchestratorService implements NotificationOrchestratorUseCase {
    private final NotificationRepositoryPort notificationRepositoryPort;

    public NotificationOrchestratorService(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public void notifyJoinRequestDecision(Long targetUserId, Long groupId, boolean approved) {
        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(targetUserId)
                .type("JOIN_REQUEST")
                .title(approved ? "Join request approved" : "Join request rejected")
                .detail("Study group #" + groupId + " updated your join request.")
                .tone(approved ? "success" : "warning")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public void notifyRsvpConfirmation(Long targetUserId, Long eventId, String eventTitle) {
        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(targetUserId)
                .type("RSVP")
                .title("RSVP confirmed")
                .detail("You are going to " + eventTitle + " (event #" + eventId + ").")
                .tone("success")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public void notifyNewMessageToGroupCreator(Long creatorUserId, Long groupId, String senderUsername) {
        notificationRepositoryPort.save(Notification.builder()
                .id(null)
                .userId(creatorUserId)
                .type("MESSAGE")
                .title("New group message")
                .detail(senderUsername + " posted a new message in study group #" + groupId + ".")
                .tone("info")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
    }
}

