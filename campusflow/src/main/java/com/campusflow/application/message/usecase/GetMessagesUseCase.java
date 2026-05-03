package com.campusflow.application.message.usecase;

import com.campusflow.application.message.dto.MessagePageResult;

public interface GetMessagesUseCase {
    MessagePageResult getMessages(Long studyGroupId, Long userId, int page, int size);
}
