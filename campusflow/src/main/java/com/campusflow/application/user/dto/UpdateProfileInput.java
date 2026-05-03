package com.campusflow.application.user.dto;

import com.campusflow.domain.user.model.ProfileVisibility;
import lombok.Value;

@Value
public class UpdateProfileInput {
    String username;
    String major;
    String interests;
    String profilePhotoUrl;
    ProfileVisibility visibility;
}
