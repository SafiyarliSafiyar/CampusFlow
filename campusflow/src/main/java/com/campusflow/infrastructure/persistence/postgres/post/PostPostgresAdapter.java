package com.campusflow.infrastructure.persistence.postgres.post;

import com.campusflow.domain.post.model.Post;
import com.campusflow.domain.post.model.PostType;
import com.campusflow.domain.post.port.PostRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostPostgresAdapter implements PostRepositoryPort {
    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        PostJpaEntity saved = postJpaRepository.save(PostJpaEntity.fromDomain(post));
        return saved.toDomain();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id).map(PostJpaEntity::toDomain);
    }

    @Override
    public List<Post> findAll(int page, int size) {
        return postJpaRepository.findAllPaged(PageRequest.of(page, size))
                .stream()
                .map(PostJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Post> findByType(PostType type, int page, int size) {
        return postJpaRepository.findByTypePaged(type, PageRequest.of(page, size))
                .stream()
                .map(PostJpaEntity::toDomain)
                .toList();
    }

    @Override
    public long countAll() {
        return postJpaRepository.count();
    }

    @Override
    public long countByType(PostType type) {
        return postJpaRepository.countByType(type);
    }

    @Override
    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }
}
