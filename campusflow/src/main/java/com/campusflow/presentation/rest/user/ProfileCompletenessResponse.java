package com.campusflow.presentation.rest.user;

import java.util.List;
import lombok.Value;

@Value
public class ProfileCompletenessResponse {
    int percent;
    List<String> missingFields;
}

