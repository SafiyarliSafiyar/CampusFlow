package com.campusflow.presentation.rest.user;

import com.campusflow.domain.user.model.ProfileVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Size(max = 80)
    private String major;

    @Size(max = 500)
    private String interests;

    @Size(max = 512)
    @Pattern(
            regexp = "^(https?://.*|/uploads/.*)?$",
            message = "profilePhotoUrl must be a valid http(s) URL or an /uploads/... path"
    )
    private String profilePhotoUrl;

    private ProfileVisibility visibility;
}
