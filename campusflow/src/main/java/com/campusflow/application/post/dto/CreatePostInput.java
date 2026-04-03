package com.campusflow.application.post.dto;

import com.campusflow.domain.post.model.PostType;
import lombok.Value;

@Value
public class CreatePostInput {
    String title;
    String content;
    PostType type;
    Long authorId;
    String authorUsername;
}
