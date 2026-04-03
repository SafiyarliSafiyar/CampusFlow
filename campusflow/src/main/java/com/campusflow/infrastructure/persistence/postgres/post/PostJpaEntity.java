package com.campusflow.infrastructure.persistence.postgres.post;

import com.campusflow.domain.post.model.Post;
import com.campusflow.domain.post.model.PostType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class PostJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Post toDomain() {
        return Post.builder()
                .id(id)
                .title(title)
                .content(content)
                .type(type)
                .authorId(authorId)
                .authorUsername(authorUsername)
                .createdAt(createdAt)
                .build();
    }

    public static PostJpaEntity fromDomain(Post post) {
        PostJpaEntity entity = new PostJpaEntity();
        entity.setId(post.getId());
        entity.setTitle(post.getTitle());
        entity.setContent(post.getContent());
        entity.setType(post.getType());
        entity.setAuthorId(post.getAuthorId());
        entity.setAuthorUsername(post.getAuthorUsername());
        entity.setCreatedAt(post.getCreatedAt());
        return entity;
    }
}
