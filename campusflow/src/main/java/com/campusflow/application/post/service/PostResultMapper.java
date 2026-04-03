package com.campusflow.application.post.service;

import com.campusflow.application.post.dto.PostResult;
import com.campusflow.domain.post.model.Post;

final class PostResultMapper {
    private PostResultMapper() {
    }

    static PostResult toResult(Post post) {
        return new PostResult(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getType().name(),
                post.getAuthorId(),
                post.getAuthorUsername(),
                post.getCreatedAt()
        );
    }
}
