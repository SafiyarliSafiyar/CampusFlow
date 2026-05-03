package com.campusflow.application.user.dto;

import java.util.List;
import lombok.Value;

@Value
public class ProfileCompletenessResult {
    int percent;
    List<String> missingFields;
}

