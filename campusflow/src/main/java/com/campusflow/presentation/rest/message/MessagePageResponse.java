package com.campusflow.presentation.rest.message;

import java.util.List;
import lombok.Value;

@Value
public class MessagePageResponse {
    List<MessageResponse> messages;
    int page;
    int size;
    long totalMessages;
    int totalPages;
}
