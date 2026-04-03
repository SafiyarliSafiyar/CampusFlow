package com.campusflow.presentation.rest.post;

import com.campusflow.application.post.dto.CreatePostInput;
import com.campusflow.application.post.dto.PostPageResult;
import com.campusflow.application.post.dto.PostResult;
import com.campusflow.application.post.usecase.CreatePostUseCase;
import com.campusflow.application.post.usecase.DeletePostUseCase;
import com.campusflow.application.post.usecase.GetFeedUseCase;
import com.campusflow.domain.post.model.PostType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class PostController {
    private final CreatePostUseCase createPostUseCase;
    private final DeletePostUseCase deletePostUseCase;
    private final GetFeedUseCase getFeedUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<PostResponse> create(@RequestBody @Valid CreatePostRequest request) {
        Long userId = extractUserId();
        String username = extractUsername();
        PostResult result = createPostUseCase.create(new CreatePostInput(
                request.getTitle(),
                request.getContent(),
                request.getType(),
                userId,
                username
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePostUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PostPageResponse> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostPageResult result = getFeedUseCase.getFeed(page, size);
        return ResponseEntity.ok(toPageResponse(result));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<PostPageResponse> getFeedByType(
            @PathVariable PostType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PostPageResult result = getFeedUseCase.getFeedByType(type, page, size);
        return ResponseEntity.ok(toPageResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(getFeedUseCase.getById(id)));
    }

    private Long extractUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long userId) {
            return userId;
        }
        throw new AccessDeniedException(
                "User id is missing from authentication context");
    }

    private String extractUsername() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return auth.getName();
    }

    private PostResponse toResponse(PostResult result) {
        return new PostResponse(
                result.getId(),
                result.getTitle(),
                result.getContent(),
                result.getType(),
                result.getAuthorId(),
                result.getAuthorUsername(),
                result.getCreatedAt()
        );
    }

    private PostPageResponse toPageResponse(PostPageResult result) {
        List<PostResponse> posts = result.getPosts().stream().map(this::toResponse).toList();
        return new PostPageResponse(
                posts,
                result.getPage(),
                result.getSize(),
                result.getTotalPosts(),
                result.getTotalPages()
        );
    }
}
