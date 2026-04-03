package com.campusflow.application.message.service;

import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.dto.SendMessageInput;
import com.campusflow.application.message.usecase.SendMessageUseCase;
import com.campusflow.domain.message.exception.EmptyMessageException;
import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.time.LocalDateTime;

public class SendMessageService implements SendMessageUseCase {
    private final MessageRepositoryPort messageRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public SendMessageService(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort
    ) {
        this.messageRepositoryPort = messageRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public MessageResult send(SendMessageInput input) {
        studyGroupRepositoryPort.findById(input.getStudyGroupId())
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
                .content(input.getContent().trim())
                .sentAt(LocalDateTime.now())
                .build();

        Message saved = messageRepositoryPort.save(message);
        return MessageResultMapper.toResult(saved);
    }
}
