package com.campusflow.infrastructure.persistence.mongo.notification;

import com.campusflow.domain.notification.model.Notification;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class NotificationDocument {
    @Id
    private String id;
    private Long userId;
    private String type;
    private String title;
    private String detail;
    private String tone;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification toDomain() {
        return Notification.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .title(title)
                .detail(detail)
                .tone(tone)
                .read(read)
                .createdAt(createdAt)
                .build();
    }

    public static NotificationDocument fromDomain(Notification notification) {
        NotificationDocument document = new NotificationDocument();
        document.setId(notification.getId());
        document.setUserId(notification.getUserId());
        document.setType(notification.getType());
        document.setTitle(notification.getTitle());
        document.setDetail(notification.getDetail());
        document.setTone(notification.getTone());
        document.setRead(notification.isRead());
        document.setCreatedAt(notification.getCreatedAt());
        return document;
    }
}

