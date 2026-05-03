package com.campusflow.infrastructure.persistence.mongo.message;

import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.model.MessageType;
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
    private MessageType type;
    private String content;
    private String attachmentUrl;
    private String attachmentName;
    private String attachmentContentType;
    private Long attachmentSizeBytes;
    private LocalDateTime sentAt;

    public Message toDomain() {
        return Message.builder()
                .id(id)
                .studyGroupId(studyGroupId)
                .senderId(senderId)
                .senderUsername(senderUsername)
                .type(type == null ? MessageType.TEXT : type)
                .content(content)
                .attachmentUrl(attachmentUrl)
                .attachmentName(attachmentName)
                .attachmentContentType(attachmentContentType)
                .attachmentSizeBytes(attachmentSizeBytes)
                .sentAt(sentAt)
                .build();
    }

    public static MessageDocument fromDomain(Message message) {
        MessageDocument document = new MessageDocument();
        document.setId(message.getId());
        document.setStudyGroupId(message.getStudyGroupId());
        document.setSenderId(message.getSenderId());
        document.setSenderUsername(message.getSenderUsername());
        document.setType(message.getType() == null ? MessageType.TEXT : message.getType());
        document.setContent(message.getContent());
        document.setAttachmentUrl(message.getAttachmentUrl());
        document.setAttachmentName(message.getAttachmentName());
        document.setAttachmentContentType(message.getAttachmentContentType());
        document.setAttachmentSizeBytes(message.getAttachmentSizeBytes());
        document.setSentAt(message.getSentAt());
        return document;
    }
}
