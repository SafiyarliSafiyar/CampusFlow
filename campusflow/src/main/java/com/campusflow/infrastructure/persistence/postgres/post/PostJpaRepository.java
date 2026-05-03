package com.campusflow.infrastructure.persistence.postgres.post;

import com.campusflow.domain.post.model.PostType;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {
    @Query("SELECT p FROM PostJpaEntity p ORDER BY p.createdAt DESC")
    List<PostJpaEntity> findAllPaged(Pageable pageable);

    @Query("SELECT p FROM PostJpaEntity p WHERE p.type = :type ORDER BY p.createdAt DESC")
    List<PostJpaEntity> findByTypePaged(@Param("type") PostType type, Pageable pageable);

    long countByType(PostType type);
}
