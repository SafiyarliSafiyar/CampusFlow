package com.campusflow.presentation.rest.post;

import com.campusflow.domain.post.model.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String content;

    @NotNull
    private PostType type;
}
