package com.campusflow.presentation.rest.game;

import com.campusflow.infrastructure.game.DailyQuizBank;
import com.campusflow.infrastructure.persistence.mongo.game.QuizScoreDocument;
import com.campusflow.infrastructure.persistence.mongo.game.QuizScoreMongoRepository;
import com.campusflow.infrastructure.persistence.mongo.game.QuizSubmissionDocument;
import com.campusflow.infrastructure.persistence.mongo.game.QuizSubmissionMongoRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final DailyQuizBank dailyQuizBank;
    private final QuizScoreMongoRepository quizScoreMongoRepository;
    private final QuizSubmissionMongoRepository quizSubmissionMongoRepository;

    @GetMapping("/daily-quiz")
    public ResponseEntity<DailyQuizResponse> getDailyQuiz() {
        DailyQuizBank.DailyQuiz quiz = dailyQuizBank.getTodayQuiz();
        List<DailyQuizResponse.Question> questions = quiz.questions().stream()
                .map(question -> new DailyQuizResponse.Question(
                        question.id(),
                        question.prompt(),
                        question.options()
                ))
                .toList();
        return ResponseEntity.ok(new DailyQuizResponse(quiz.quizDate(), questions));
    }

    @PostMapping("/daily-quiz/submit")
    public ResponseEntity<QuizSubmissionResponse> submitDailyQuiz(@RequestBody @Valid SubmitDailyQuizRequest request) {
        Long userId = extractUserId();
        String username = extractUsername();

        DailyQuizBank.DailyQuiz quiz = dailyQuizBank.getTodayQuiz();

        QuizSubmissionDocument existingSubmission = quizSubmissionMongoRepository
                .findByUserIdAndQuizDate(userId, quiz.quizDate())
                .orElse(null);

        QuizScoreDocument scoreDoc = quizScoreMongoRepository.findById(userId).orElse(null);
        if (scoreDoc == null) {
            scoreDoc = new QuizScoreDocument();
            scoreDoc.setUserId(userId);
            scoreDoc.setUsername(username);
            scoreDoc.setTotalPoints(0);
            scoreDoc.setUpdatedAt(LocalDateTime.now());
        } else if (username != null && !username.isBlank()) {
            scoreDoc.setUsername(username);
        }

        if (existingSubmission != null) {
            // already counted today; return current total points
            return ResponseEntity.ok(new QuizSubmissionResponse(existingSubmission.getScore(), scoreDoc.getTotalPoints()));
        }

        Map<String, DailyQuizBank.DailyQuizQuestion> questionMap = quiz.questions().stream()
                .collect(Collectors.toMap(DailyQuizBank.DailyQuizQuestion::id, Function.identity()));

        // score is number of correct answers (max questions count)
        int score = 0;
        Set<String> seen = request.getAnswers().stream().map(SubmitDailyQuizRequest.Answer::getQuestionId).collect(Collectors.toSet());
        for (String requiredId : questionMap.keySet()) {
            if (!seen.contains(requiredId)) {
                // missing answers is allowed; just don't score
            }
        }

        for (SubmitDailyQuizRequest.Answer answer : request.getAnswers()) {
            DailyQuizBank.DailyQuizQuestion question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                continue;
            }
            if (answer.getSelectedOptionIndex() != null && answer.getSelectedOptionIndex() == question.correctOptionIndex()) {
                score += 1;
            }
        }

        QuizSubmissionDocument submission = new QuizSubmissionDocument();
        submission.setId(userId + ":" + quiz.quizDate());
        submission.setUserId(userId);
        submission.setQuizDate(quiz.quizDate());
        submission.setScore(score);
        submission.setSubmittedAt(LocalDateTime.now());
        quizSubmissionMongoRepository.save(submission);

        scoreDoc.setTotalPoints(scoreDoc.getTotalPoints() + score);
        scoreDoc.setUpdatedAt(LocalDateTime.now());
        quizScoreMongoRepository.save(scoreDoc);

        return ResponseEntity.ok(new QuizSubmissionResponse(score, scoreDoc.getTotalPoints()));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @RequestParam(defaultValue = "5") int limit
    ) {
        int size = Math.min(Math.max(limit, 1), 50);
        List<LeaderboardEntryResponse> entries = quizScoreMongoRepository
                .findTopByOrderByTotalPointsDesc(PageRequest.of(0, size))
                .stream()
                .map(doc -> new LeaderboardEntryResponse(doc.getUserId(), doc.getUsername(), doc.getTotalPoints()))
                .toList();
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/path-puzzle/leaderboard")
    public ResponseEntity<List<Object>> getPathPuzzleLeaderboard(@RequestParam(defaultValue = "5") int limit) {
        // Optional feature: keep endpoint to avoid 404s in the frontend. Implement later.
        return ResponseEntity.ok(List.of());
    }

    private Long extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException("User id is missing from authentication context");
    }

    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return auth.getName();
    }
}

