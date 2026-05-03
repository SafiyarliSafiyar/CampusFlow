package com.campusflow.application.notification.usecase;

import com.campusflow.domain.notification.model.Notification;
import java.util.List;

public interface GetNotificationsUseCase {
    List<Notification> getLatest(Long userId, int limit);
}

