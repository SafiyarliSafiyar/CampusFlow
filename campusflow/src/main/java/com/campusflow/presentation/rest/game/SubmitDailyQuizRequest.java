package com.campusflow.presentation.rest.game;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class SubmitDailyQuizRequest {
    @NotEmpty
    @Size(max = 20)
    @Valid
    private List<Answer> answers;

    @Data
    public static class Answer {
        @NotNull
        private String questionId;

        @NotNull
        private Integer selectedOptionIndex;
    }
}

