package com.campusflow.application.message.service;

import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.dto.SendMessageInput;
import com.campusflow.application.message.usecase.SendMessageUseCase;
import com.campusflow.domain.message.exception.EmptyMessageException;
import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.model.MessageType;
import com.campusflow.domain.message.port.MessagePushPort;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.application.notification.usecase.NotificationOrchestratorUseCase;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.model.StudyGroup;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDateTime;

public class SendMessageService implements SendMessageUseCase {
    private final MessageRepositoryPort messageRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;
    private final MessagePushPort messagePushPort;
    private final NotificationOrchestratorUseCase notificationOrchestratorUseCase;

    public SendMessageService(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort,
            MessagePushPort messagePushPort,
            NotificationOrchestratorUseCase notificationOrchestratorUseCase
    ) {
        this.messageRepositoryPort = messageRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
        this.messagePushPort = messagePushPort;
        this.notificationOrchestratorUseCase = notificationOrchestratorUseCase;
    }

    @Override
    public MessageResult send(SendMessageInput input) {
        StudyGroup group = studyGroupRepositoryPort.findById(input.getStudyGroupId())
                .orElseThrow(() -> new StudyGroupNotFoundException(input.getStudyGroupId()));

        if (!studyGroupRepositoryPort.isMember(input.getSenderId(), input.getStudyGroupId())) {
            throw new MessageAccessDeniedException();
        }

        if (input.getContent() == null || input.getContent().trim().isEmpty()) {
            throw new EmptyMessageException();
        }

        Message message = Message.builder()
                .id(null)
                .studyGroupId(input.getStudyGroupId())
                .senderId(input.getSenderId())
                .senderUsername(input.getSenderUsername())
                .type(MessageType.TEXT)
                .content(input.getContent().trim())
                .attachmentUrl(null)
                .attachmentName(null)
                .attachmentContentType(null)
                .attachmentSizeBytes(null)
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
        messagePushPort.broadcastMessage(input.getStudyGroupId(), payload);

        if (group.getCreatorId() != null && !group.getCreatorId().equals(input.getSenderId())) {
            notificationOrchestratorUseCase.notifyNewMessageToGroupCreator(
                    group.getCreatorId(),
                    group.getId(),
                    input.getSenderUsername()
            );
        }

        return MessageResultMapper.toResult(saved);
    }
}
