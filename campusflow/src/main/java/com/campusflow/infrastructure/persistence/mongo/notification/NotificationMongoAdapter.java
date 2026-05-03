package com.campusflow.infrastructure.persistence.mongo.notification;

import com.campusflow.domain.notification.model.Notification;
import com.campusflow.domain.notification.port.NotificationRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationMongoAdapter implements NotificationRepositoryPort {
    private final NotificationMongoRepository notificationMongoRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationDocument saved = notificationMongoRepository.save(NotificationDocument.fromDomain(notification));
        return saved.toDomain();
    }

    @Override
    public List<Notification> findLatestForUser(Long userId, int limit) {
        return notificationMongoRepository.findByUserIdOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(0, Math.max(1, limit))
                ).stream()
                .map(NotificationDocument::toDomain)
                .toList();
    }

    @Override
    public void markAllRead(Long userId) {
        // naive implementation: fetch latest batch and mark read
        List<NotificationDocument> docs = notificationMongoRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(0, 500)
        );
        if (docs.isEmpty()) {
            return;
        }
        docs.forEach(doc -> doc.setRead(true));
        notificationMongoRepository.saveAll(docs);
    }
}

