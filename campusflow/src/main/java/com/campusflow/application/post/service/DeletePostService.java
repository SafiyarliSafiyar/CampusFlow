package com.campusflow.application.post.service;

import com.campusflow.application.post.usecase.DeletePostUseCase;
import com.campusflow.domain.post.exception.PostNotFoundException;
import com.campusflow.domain.post.port.PostRepositoryPort;

public class DeletePostService implements DeletePostUseCase {
    private final PostRepositoryPort postRepositoryPort;

    public DeletePostService(PostRepositoryPort postRepositoryPort) {
        this.postRepositoryPort = postRepositoryPort;
    }

    @Override
    public void delete(Long postId) {
        postRepositoryPort.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        postRepositoryPort.deleteById(postId);
    }
}
