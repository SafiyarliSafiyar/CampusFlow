package com.campusflow.presentation.rest.message;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class MessageResponse {
    String id;
    Long studyGroupId;
    Long senderId;
    String senderUsername;
    String content;
    LocalDateTime sentAt;
}
