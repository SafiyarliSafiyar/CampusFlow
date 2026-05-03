package com.campusflow.domain.post.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Post {
    private final Long id;
    private final String title;
    private final String content;
    private final PostType type;
    private final Long authorId;
    private final String authorUsername;
    private final LocalDateTime createdAt;
}
