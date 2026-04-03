package com.campusflow.domain.message.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Message {
    private final String id;
    private final Long studyGroupId;
    private final Long senderId;
    private final String senderUsername;
    private final String content;
    private final LocalDateTime sentAt;
}
