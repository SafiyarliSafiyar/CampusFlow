package com.campusflow.application.notification.usecase;

public interface NotificationOrchestratorUseCase {
    void notifyJoinRequestDecision(Long targetUserId, Long groupId, boolean approved);

    void notifyRsvpConfirmation(Long targetUserId, Long eventId, String eventTitle);

    void notifyNewMessageToGroupCreator(Long creatorUserId, Long groupId, String senderUsername);
}

