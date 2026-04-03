package com.campusflow.presentation.rest.post;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class PostResponse {
    Long id;
    String title;
    String content;
    String type;
    Long authorId;
    String authorUsername;
    LocalDateTime createdAt;
}
