package com.campusflow.presentation.rest.game;

import java.time.LocalDate;
import java.util.List;
import lombok.Value;

@Value
public class DailyQuizResponse {
    LocalDate quizDate;
    List<Question> questions;

    @Value
    public static class Question {
        String id;
        String prompt;
        List<String> options;
    }
}

