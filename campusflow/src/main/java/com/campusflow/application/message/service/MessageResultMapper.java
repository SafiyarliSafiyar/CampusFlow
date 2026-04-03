package com.campusflow.application.message.service;

import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.domain.message.model.Message;

final class MessageResultMapper {
    private MessageResultMapper() {
    }

    static MessageResult toResult(Message message) {
        return new MessageResult(
                message.getId(),
                message.getStudyGroupId(),
                message.getSenderId(),
                message.getSenderUsername(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
