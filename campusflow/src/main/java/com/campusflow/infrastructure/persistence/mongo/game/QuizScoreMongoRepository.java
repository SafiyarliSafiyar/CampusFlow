package com.campusflow.infrastructure.persistence.mongo.game;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface QuizScoreMongoRepository extends MongoRepository<QuizScoreDocument, Long> {
    @Query(value = "{}", sort = "{ totalPoints: -1, updatedAt: -1 }")
    List<QuizScoreDocument> findTopByOrderByTotalPointsDesc(org.springframework.data.domain.Pageable pageable);
}

