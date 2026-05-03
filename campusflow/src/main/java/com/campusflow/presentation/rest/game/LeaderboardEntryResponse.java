package com.campusflow.presentation.rest.game;

import lombok.Value;

@Value
public class LeaderboardEntryResponse {
    Long userId;
    String username;
    int totalPoints;
}

