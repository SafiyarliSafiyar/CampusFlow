package com.campusflow.infrastructure.persistence.mongo.game;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizSubmissionMongoRepository extends MongoRepository<QuizSubmissionDocument, String> {
    Optional<QuizSubmissionDocument> findByUserIdAndQuizDate(Long userId, LocalDate quizDate);
}

