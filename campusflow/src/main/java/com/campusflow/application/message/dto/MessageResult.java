package com.campusflow.application.message.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class MessageResult {
    String id;
    Long studyGroupId;
    Long senderId;
    String senderUsername;
    String content;
    LocalDateTime sentAt;
}
