package com.campusflow.application.message.service;

import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.usecase.SendFileMessageUseCase;
import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.model.MessageType;
import com.campusflow.domain.message.port.MessagePushPort;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import com.campusflow.infrastructure.storage.LocalUploadsStorage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public class SendFileMessageService implements SendFileMessageUseCase {
    private final MessageRepositoryPort messageRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;
    private final MessagePushPort messagePushPort;
    private final NotificationOrchestratorUseCase notificationOrchestratorUseCase;
    private final LocalUploadsStorage localUploadsStorage;

    public SendFileMessageService(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            MessagePushPort messagePushPort,
            NotificationOrchestratorUseCase notificationOrchestratorUseCase,
            LocalUploadsStorage localUploadsStorage
    ) {
        this.messageRepositoryPort = messageRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
        this.messagePushPort = messagePushPort;
        this.notificationOrchestratorUseCase = notificationOrchestratorUseCase;
        this.localUploadsStorage = localUploadsStorage;
    }

    @Override
    public MessageResult sendFile(Long studyGroupId, Long senderId, String senderUsername, MultipartFile file) throws IOException {
        StudyGroup group = studyGroupRepositoryPort.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));

        if (!studyGroupRepositoryPort.isMember(senderId, studyGroupId)) {
            throw new MessageAccessDeniedException();
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")
                && !contentType.toLowerCase().startsWith("application/")
                && !contentType.toLowerCase().startsWith("text/")) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        if (file.getSize() > 10L * 1024L * 1024L) {
            throw new IllegalArgumentException("File too large");
        }

        String uploadedUrl = localUploadsStorage.storeProfilePhoto(file);

        Message message = Message.builder()
                .id(null)
                .studyGroupId(studyGroupId)
                .senderId(senderId)
                .senderUsername(senderUsername)
                .type(MessageType.FILE)
                .content(file.getOriginalFilename())
                .attachmentUrl(uploadedUrl)
                .attachmentName(file.getOriginalFilename())
                .attachmentContentType(contentType)
                .attachmentSizeBytes(file.getSize())
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepositoryPort.save(message);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", saved.getId());
        payload.put("studyGroupId", saved.getStudyGroupId());
        payload.put("senderId", saved.getSenderId());
        payload.put("senderUsername", saved.getSenderUsername());
        payload.put("type", saved.getType() == null ? null : saved.getType().name());
        payload.put("content", saved.getContent());
        payload.put("attachmentUrl", saved.getAttachmentUrl());
        payload.put("attachmentName", saved.getAttachmentName());
        payload.put("attachmentContentType", saved.getAttachmentContentType());
        payload.put("attachmentSizeBytes", saved.getAttachmentSizeBytes());
        payload.put("sentAt", saved.getSentAt().toString());
        messagePushPort.broadcastMessage(studyGroupId, payload);

        if (group.getCreatorId() != null && !group.getCreatorId().equals(senderId)) {
            notificationOrchestratorUseCase.notifyNewMessageToGroupCreator(
                    group.getCreatorId(),
                    group.getId(),
                    senderUsername
            );
        }

        return MessageResultMapper.toResult(saved);
    }
}

