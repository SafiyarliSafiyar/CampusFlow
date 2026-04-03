package com.campusflow.application.post.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class PostResult {
    Long id;
    String title;
    String content;
    String type;
    Long authorId;
    String authorUsername;
    LocalDateTime createdAt;
}
