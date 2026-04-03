package com.campusflow.presentation.rest.post;

import java.util.List;
import lombok.Value;

@Value
public class PostPageResponse {
    List<PostResponse> posts;
    int page;
    int size;
    long totalPosts;
    int totalPages;
}
