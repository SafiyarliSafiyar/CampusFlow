package com.campusflow.domain.notification.port;

import com.campusflow.domain.notification.model.Notification;
import java.util.List;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    List<Notification> findLatestForUser(Long userId, int limit);

    void markAllRead(Long userId);
}

