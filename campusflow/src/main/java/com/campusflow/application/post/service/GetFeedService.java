package com.campusflow.application.post.service;

import com.campusflow.application.post.dto.PostPageResult;
import com.campusflow.application.post.dto.PostResult;
import com.campusflow.application.post.usecase.GetFeedUseCase;
import com.campusflow.domain.post.exception.PostNotFoundException;
import com.campusflow.domain.post.model.PostType;
import com.campusflow.domain.post.port.PostRepositoryPort;
import java.util.List;

public class GetFeedService implements GetFeedUseCase {
    private final PostRepositoryPort postRepositoryPort;

    public GetFeedService(PostRepositoryPort postRepositoryPort) {
        this.postRepositoryPort = postRepositoryPort;
    }

    @Override
    public PostPageResult getFeed(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 20;
        List<PostResult> posts = postRepositoryPort.findAll(safePage, safeSize)
                .stream()
                .map(PostResultMapper::toResult)
                .toList();
        long totalPosts = postRepositoryPort.countAll();
        int totalPages = (int) Math.ceil((double) totalPosts / safeSize);
        return new PostPageResult(posts, safePage, safeSize, totalPosts, totalPages);
    }

    @Override
    public PostPageResult getFeedByType(PostType type, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 20;
        List<PostResult> posts = postRepositoryPort.findByType(type, safePage, safeSize)
                .stream()
                .map(PostResultMapper::toResult)
                .toList();
        long totalPosts = postRepositoryPort.countByType(type);
        int totalPages = (int) Math.ceil((double) totalPosts / safeSize);
        return new PostPageResult(posts, safePage, safeSize, totalPosts, totalPages);
    }

    @Override
    public PostResult getById(Long id) {
        return postRepositoryPort.findById(id)
                .map(PostResultMapper::toResult)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
}
