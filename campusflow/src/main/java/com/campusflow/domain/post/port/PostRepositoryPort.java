package com.campusflow.domain.post.port;

import com.campusflow.domain.post.model.Post;
import com.campusflow.domain.post.model.PostType;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Post save(Post post);

    Optional<Post> findById(Long id);

    List<Post> findAll(int page, int size);

    List<Post> findByType(PostType type, int page, int size);

    long countAll();

    long countByType(PostType type);

    void deleteById(Long id);
}
