package com.campusflow.application.message.usecase;

import com.campusflow.application.message.dto.MessageResult;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface SendFileMessageUseCase {
    MessageResult sendFile(Long studyGroupId, Long senderId, String senderUsername, MultipartFile file) throws IOException;
}

