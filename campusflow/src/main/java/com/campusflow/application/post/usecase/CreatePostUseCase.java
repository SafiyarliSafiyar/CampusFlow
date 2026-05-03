package com.campusflow.application.post.usecase;

import com.campusflow.application.post.dto.CreatePostInput;
import com.campusflow.application.post.dto.PostResult;

public interface CreatePostUseCase {
    PostResult create(CreatePostInput input);
}
