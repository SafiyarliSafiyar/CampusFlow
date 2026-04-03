package com.campusflow.application.post.usecase;

import com.campusflow.application.post.dto.PostPageResult;
import com.campusflow.application.post.dto.PostResult;
import com.campusflow.domain.post.model.PostType;

public interface GetFeedUseCase {
    PostPageResult getFeed(int page, int size);

    PostPageResult getFeedByType(PostType type, int page, int size);

    PostResult getById(Long id);
}
