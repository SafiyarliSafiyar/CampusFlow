package com.campusflow.infrastructure.persistence.mongo.notification;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

