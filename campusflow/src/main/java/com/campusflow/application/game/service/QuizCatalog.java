package com.campusflow.application.game.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

final class QuizCatalog {
    private static final List<QuizCatalogQuestion> QUESTION_BANK = List.of(
            new QuizCatalogQuestion(
                    "cf-quiz-1",
                    "Which OOP principle keeps an object's internal state hidden behind public methods?",
                    List.of("Encapsulation", "Inheritance", "Polymorphism", "Serialization"),
                    0,
                    "Object-oriented programming"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-2",
                    "In SOLID, which principle says a class should have only one reason to change?",
                    List.of("Liskov Substitution Principle", "Single Responsibility Principle", "Interface Segregation Principle", "Open/Closed Principle"),
                    1,
                    "Object-oriented programming"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-3",
                    "What is the main advantage of composition over inheritance in object-oriented design?",
                    List.of("It avoids using objects", "It always reduces memory usage", "It makes behavior easier to swap and reuse", "It removes the need for interfaces"),
                    2,
                    "Object-oriented programming"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-4",
                    "Which design pattern is commonly used when creating an object step by step with optional fields?",
                    List.of("Builder", "Observer", "Adapter", "Singleton"),
                    1,
                    "Object-oriented programming"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-5",
                    "If a subclass can replace its parent class without breaking behavior, which SOLID principle is being followed?",
                    List.of("Dependency Inversion Principle", "Open/Closed Principle", "Liskov Substitution Principle", "Single Responsibility Principle"),
                    2,
                    "Object-oriented programming"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-6",
                    "What is the main job of a load balancer in system design?",
                    List.of("Encrypt every database row", "Distribute traffic across multiple servers", "Compile frontend assets", "Store user sessions permanently"),
                    1,
                    "System design"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-7",
                    "Which choice best explains why caching is useful in a high-traffic system?",
                    List.of("It increases request latency", "It makes every query strongly consistent", "It reduces repeated expensive reads", "It replaces the database schema"),
                    2,
                    "System design"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-8",
                    "What is an index mainly used for in a relational database?",
                    List.of("Making writes impossible", "Speeding up lookups on selected columns", "Replacing primary keys everywhere", "Compressing image uploads"),
                    1,
                    "System design"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-9",
                    "Horizontal scaling means:",
                    List.of("Adding more servers to handle the workload", "Increasing the CPU on a single server", "Switching from SQL to NoSQL", "Reducing the number of API endpoints"),
                    0,
                    "System design"
            ),
            new QuizCatalogQuestion(
                    "cf-quiz-10",
                    "When is a message queue especially helpful in system design?",
                    List.of("When work should be processed asynchronously", "When every request must be blocked until all emails are sent", "When avoiding retries completely", "When removing background jobs"),
                    2,
                    "System design"
            )
    );

    private static final int DAILY_QUESTION_COUNT = 5;

    private QuizCatalog() {
    }

    static List<QuizCatalogQuestion> getDailyQuestions() {
        int bankSize = QUESTION_BANK.size();
        int startIndex = LocalDate.now().getDayOfYear() % bankSize;
        List<QuizCatalogQuestion> selected = new ArrayList<>();

        for (int offset = 0; offset < DAILY_QUESTION_COUNT; offset++) {
            selected.add(QUESTION_BANK.get((startIndex + offset) % bankSize));
        }

        return selected;
    }

    record QuizCatalogQuestion(
            String id,
            String prompt,
            List<String> options,
            int correctOptionIndex,
            String category
    ) {
    }
}
