package com.campusflow.application.message.dto;

import java.util.List;
import lombok.Value;

@Value
public class MessagePageResult {
    List<MessageResult> messages;
    int page;
    int size;
    long totalMessages;
    int totalPages;
}
