package com.campusflow.infrastructure.game;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DailyQuizBank {
    public DailyQuiz getTodayQuiz() {
        LocalDate today = LocalDate.now();
        return new DailyQuiz(
                today,
                List.of(
                        new DailyQuizQuestion(
                                "oop-1",
                                "Which OOP principle hides internal state and exposes behavior via methods?",
                                List.of("Inheritance", "Encapsulation", "Polymorphism", "Abstraction"),
                                1
                        ),
                        new DailyQuizQuestion(
                                "oop-2",
                                "In Java, method overriding is most closely related to:",
                                List.of("Encapsulation", "Polymorphism", "Composition", "Immutability"),
                                1
                        ),
                        new DailyQuizQuestion(
                                "sys-1",
                                "Which component helps distribute traffic across multiple servers?",
                                List.of("Load balancer", "Cache", "Database index", "Message schema"),
                                0
                        ),
                        new DailyQuizQuestion(
                                "sys-2",
                                "What does 'idempotent' mean for an API endpoint?",
                                List.of(
                                        "It always returns JSON",
                                        "It can be called multiple times with the same effect",
                                        "It never fails",
                                        "It requires authentication"
                                ),
                                1
                        ),
                        new DailyQuizQuestion(
                                "sys-3",
                                "Which storage is best for fast key lookups and caching?",
                                List.of("Redis", "HDD archive", "PDF files", "Email inbox"),
                                0
                        )
                )
        );
    }

    public record DailyQuiz(LocalDate quizDate, List<DailyQuizQuestion> questions) {
    }

    public record DailyQuizQuestion(String id, String prompt, List<String> options, int correctOptionIndex) {
    }
}

