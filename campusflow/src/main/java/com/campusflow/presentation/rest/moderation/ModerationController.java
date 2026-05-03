package com.campusflow.presentation.rest.moderation;

import com.campusflow.application.audit.dto.ReportContentInput;
import com.campusflow.application.audit.dto.ReportContentResult;
import com.campusflow.application.audit.usecase.GetAuditLogUseCase;
import com.campusflow.application.audit.usecase.GetModerationQueueUseCase;
import com.campusflow.application.audit.usecase.ReportContentUseCase;
import com.campusflow.domain.audit.model.AuditLogEntry;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
public class ModerationController {
    private final ReportContentUseCase reportContentUseCase;
    private final GetAuditLogUseCase getAuditLogUseCase;
    private final GetModerationQueueUseCase getModerationQueueUseCase;

    @PostMapping("/reports")
    public ResponseEntity<ReportContentResponse> report(@RequestBody @Valid ReportContentRequest request) {
        Long userId = extractUserId();
        ReportContentResult result = reportContentUseCase.report(new ReportContentInput(
                userId,
                request.getTargetType(),
                request.getTargetId(),
                request.getReason(),
                request.getDetail()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReportContentResponse(
                result.getId(),
                result.getActorUserId(),
                result.getTargetType(),
                result.getTargetId(),
                result.getReason(),
                result.getDetail(),
                result.getCreatedAt()
        ));
    }

    @GetMapping("/audit")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<List<AuditLogEntryResponse>> audit(@RequestParam(defaultValue = "50") int limit) {
        List<AuditLogEntry> entries = getAuditLogUseCase.getRecent(Math.min(Math.max(limit, 1), 200));
        List<AuditLogEntryResponse> response = entries.stream()
                .map(entry -> new AuditLogEntryResponse(
                        entry.getId(),
                        entry.getActorUserId(),
                        entry.getAction(),
                        entry.getTargetType(),
                        entry.getTargetId(),
                        entry.getDetail(),
                        entry.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<List<AuditLogEntryResponse>> queue(@RequestParam(defaultValue = "50") int limit) {
        List<AuditLogEntry> entries = getModerationQueueUseCase.getRecentReports(Math.min(Math.max(limit, 1), 200));
        List<AuditLogEntryResponse> response = entries.stream()
                .map(entry -> new AuditLogEntryResponse(
                        entry.getId(),
                        entry.getActorUserId(),
                        entry.getAction(),
                        entry.getTargetType(),
                        entry.getTargetId(),
                        entry.getDetail(),
                        entry.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException(
                "User id is missing from authentication context");
    }
}
