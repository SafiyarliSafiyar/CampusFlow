package com.campusflow.application.post.dto;

import java.util.List;
import lombok.Value;

@Value
public class PostPageResult {
    List<PostResult> posts;
    int page;
    int size;
    long totalPosts;
    int totalPages;
}
