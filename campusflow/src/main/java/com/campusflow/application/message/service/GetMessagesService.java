package com.campusflow.application.message.service;

import com.campusflow.application.message.dto.MessagePageResult;
import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.usecase.GetMessagesUseCase;
import com.campusflow.domain.message.exception.MessageAccessDeniedException;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import com.campusflow.domain.studygroup.exception.StudyGroupNotFoundException;
import com.campusflow.domain.studygroup.port.StudyGroupRepositoryPort;
import java.util.List;

public class GetMessagesService implements GetMessagesUseCase {
    private final MessageRepositoryPort messageRepositoryPort;
    private final StudyGroupRepositoryPort studyGroupRepositoryPort;

    public GetMessagesService(
            MessageRepositoryPort messageRepositoryPort,
            StudyGroupRepositoryPort studyGroupRepositoryPort
    ) {
        this.messageRepositoryPort = messageRepositoryPort;
        this.studyGroupRepositoryPort = studyGroupRepositoryPort;
    }

    @Override
    public MessagePageResult getMessages(Long studyGroupId, Long userId, int page, int size) {
        studyGroupRepositoryPort.findById(studyGroupId)
                .orElseThrow(() -> new StudyGroupNotFoundException(studyGroupId));

        if (!studyGroupRepositoryPort.isMember(userId, studyGroupId)) {
            throw new MessageAccessDeniedException();
        }

        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 50;

        List<MessageResult> messages = messageRepositoryPort.findByStudyGroupId(studyGroupId, safePage, safeSize)
                .stream()
                .map(MessageResultMapper::toResult)
                .toList();

        long totalMessages = messageRepositoryPort.countByStudyGroupId(studyGroupId);
        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil((double) totalMessages / safeSize);

        return new MessagePageResult(messages, safePage, safeSize, totalMessages, totalPages);
    }
}
