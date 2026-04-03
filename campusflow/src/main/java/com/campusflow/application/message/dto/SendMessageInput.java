package com.campusflow.application.message.dto;

import lombok.Value;

@Value
public class SendMessageInput {
    Long studyGroupId;
    Long senderId;
    String senderUsername;
    String content;
}
