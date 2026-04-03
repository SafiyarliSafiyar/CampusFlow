package com.campusflow.infrastructure.persistence.mongo.message;

import com.campusflow.domain.message.model.Message;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor
public class MessageDocument {
    @Id
    private String id;
    private Long studyGroupId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;

    public Message toDomain() {
        return Message.builder()
                .id(id)
                .studyGroupId(studyGroupId)
                .senderId(senderId)
                .senderUsername(senderUsername)
                .content(content)
                .sentAt(sentAt)
                .build();
    }

    public static MessageDocument fromDomain(Message message) {
        MessageDocument document = new MessageDocument();
        document.setId(message.getId());
        document.setStudyGroupId(message.getStudyGroupId());
        document.setSenderId(message.getSenderId());
        document.setSenderUsername(message.getSenderUsername());
        document.setContent(message.getContent());
        document.setSentAt(message.getSentAt());
        return document;
    }
}
