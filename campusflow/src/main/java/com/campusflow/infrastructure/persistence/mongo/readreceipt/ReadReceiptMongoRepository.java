package com.campusflow.infrastructure.persistence.mongo.readreceipt;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadReceiptMongoRepository extends MongoRepository<ReadReceiptDocument, String> {
    Optional<ReadReceiptDocument> findByMessageIdAndUserId(String messageId, Long userId);

    List<ReadReceiptDocument> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);
}

