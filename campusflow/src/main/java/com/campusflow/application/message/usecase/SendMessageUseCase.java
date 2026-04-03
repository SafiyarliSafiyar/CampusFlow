package com.campusflow.application.message.usecase;

import com.campusflow.application.message.dto.MessageResult;
import com.campusflow.application.message.dto.SendMessageInput;

public interface SendMessageUseCase {
    MessageResult send(SendMessageInput input);
}
