package com.campusflow.infrastructure.persistence.mongo.message;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {
    List<MessageDocument> findByStudyGroupIdOrderBySentAtDesc(Long studyGroupId, Pageable pageable);

    long countByStudyGroupId(Long studyGroupId);
}
