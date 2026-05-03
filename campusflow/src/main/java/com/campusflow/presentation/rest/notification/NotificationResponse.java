package com.campusflow.presentation.rest.notification;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class NotificationResponse {
    String id;
    String type;
    String title;
    String detail;
    String tone;
    boolean read;
    LocalDateTime createdAt;
}

