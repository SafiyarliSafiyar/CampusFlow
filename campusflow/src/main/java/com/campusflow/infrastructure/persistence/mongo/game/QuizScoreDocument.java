package com.campusflow.infrastructure.persistence.mongo.game;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_scores")
@Getter
@Setter
@NoArgsConstructor
public class QuizScoreDocument {
    @Id
    private Long userId;
    private String username;
    private int totalPoints;
    private LocalDateTime updatedAt;
}

