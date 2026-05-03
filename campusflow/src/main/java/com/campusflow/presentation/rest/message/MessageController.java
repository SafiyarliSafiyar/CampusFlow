package com.campusflow.presentation.rest.message;

import com.campusflow.application.message.dto.MessagePageResult;
import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.dto.SendMessageInput;
import com.campusflow.application.message.usecase.GetMessagesUseCase;
import com.campusflow.application.message.usecase.SendMessageUseCase;
import com.campusflow.application.message.usecase.SendFileMessageUseCase;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/study-groups/{groupId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;
    private final SendFileMessageUseCase sendFileMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @PathVariable Long groupId,
            @RequestBody @Valid SendMessageRequest request
    ) {
        Long userId = extractUserId();
        String username = extractUsername();
        MessageResult result = sendMessageUseCase.send(
                new SendMessageInput(groupId, userId, username, request.getContent())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendFile(
            @PathVariable Long groupId,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        Long userId = extractUserId();
        String username = extractUsername();
        MessageResult result = sendFileMessageUseCase.sendFile(groupId, userId, username, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<MessagePageResponse> getMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Long userId = extractUserId();
        MessagePageResult result = getMessagesUseCase.getMessages(groupId, userId, page, size);
        List<MessageResponse> messages = result.getMessages().stream().map(this::toResponse).toList();
        MessagePageResponse response = new MessagePageResponse(
                messages,
                result.getPage(),
                result.getSize(),
                result.getTotalMessages(),
                result.getTotalPages()
        );
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

    private String extractUsername() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return auth.getName();
    }

    private MessageResponse toResponse(MessageResult result) {
        return new MessageResponse(
                result.getId(),
                result.getStudyGroupId(),
                result.getSenderId(),
                result.getSenderUsername(),
                result.getType(),
                result.getContent(),
                result.getAttachmentUrl(),
                result.getAttachmentName(),
                result.getAttachmentContentType(),
                result.getAttachmentSizeBytes(),
                result.getSentAt()
        );
    }
}
