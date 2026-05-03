package com.campusflow.infrastructure.persistence.mongo.game;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_submissions")
@Getter
@Setter
@NoArgsConstructor
public class QuizSubmissionDocument {
    @Id
    private String id; // userId:quizDate
    private Long userId;
    private LocalDate quizDate;
    private int score;
    private LocalDateTime submittedAt;
}

