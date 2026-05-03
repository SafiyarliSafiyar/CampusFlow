package com.campusflow.application.post.service;

import com.campusflow.application.post.dto.CreatePostInput;
import com.campusflow.application.post.dto.PostResult;
import com.campusflow.application.post.usecase.CreatePostUseCase;
import com.campusflow.domain.post.model.Post;
import com.campusflow.domain.post.port.PostRepositoryPort;
import java.time.LocalDateTime;

public class CreatePostService implements CreatePostUseCase {
    private final PostRepositoryPort postRepositoryPort;

    public CreatePostService(PostRepositoryPort postRepositoryPort) {
        this.postRepositoryPort = postRepositoryPort;
    }

    @Override
    public PostResult create(CreatePostInput input) {
        Post post = Post.builder()
                .id(null)
                .title(input.getTitle())
                .content(input.getContent())
                .type(input.getType())
                .authorId(input.getAuthorId())
                .authorUsername(input.getAuthorUsername())
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepositoryPort.save(post);
        return PostResultMapper.toResult(saved);
    }
}
