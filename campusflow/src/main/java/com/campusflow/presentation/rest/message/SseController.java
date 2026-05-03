package com.campusflow.presentation.rest.message;

import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.domain.user.port.TokenProviderPort;
import com.campusflow.domain.user.port.UserRepositoryPort;
import com.campusflow.infrastructure.sse.SseEmitterRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/study-groups")
public class SseController {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TokenProviderPort tokenProviderPort;

    public SseController(
            SseEmitterRegistry sseEmitterRegistry,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            TokenProviderPort tokenProviderPort
    ) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @GetMapping("/{groupId}/stream")
    public SseEmitter streamMessages(
            @PathVariable Long groupId,
            @RequestParam(required = false) String token,
            Authentication authentication
    ) {
        Long userId = null;
        if (authentication != null && authentication.getDetails() instanceof Long) {
            userId = (Long) authentication.getDetails();
        } else if (token != null) {
            if (!tokenProviderPort.isTokenValid(token)) {
                throw new MessageAccessDeniedException();
            }
            userId = tokenProviderPort.extractUserId(token);
        }

        if (userId == null) {
            throw new MessageAccessDeniedException();
        }

        boolean isMember = studyGroupRepositoryPort.isMember(userId, groupId);
        if (!isMember) {
            throw new MessageAccessDeniedException();
        }

        SseEmitter emitter = new SseEmitter(0L);
        sseEmitterRegistry.addEmitter(groupId, emitter);

        emitter.onCompletion(() -> sseEmitterRegistry.removeEmitter(groupId, emitter));
        emitter.onTimeout(() -> sseEmitterRegistry.removeEmitter(groupId, emitter));
        emitter.onError(e -> sseEmitterRegistry.removeEmitter(groupId, emitter));

        try {
            emitter.send(SseEmitter.event().name("ping").data("connected"));
        } catch (Exception _e) {
            sseEmitterRegistry.removeEmitter(groupId, emitter);
        }

        return emitter;
    }
}

