package com.campusflow.infrastructure.persistence.mongo.readreceipt;

import com.campusflow.domain.message.model.ReadReceipt;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message_reads")
@Getter
@Setter
@NoArgsConstructor
public class ReadReceiptDocument {
    @Id
    private String id;
    private Long studyGroupId;
    private String messageId;
    private Long userId;
    private LocalDateTime readAt;

    public ReadReceipt toDomain() {
        return ReadReceipt.builder()
                .id(id)
                .studyGroupId(studyGroupId)
                .messageId(messageId)
                .userId(userId)
                .readAt(readAt)
                .build();
    }

    public static ReadReceiptDocument fromDomain(ReadReceipt receipt) {
        ReadReceiptDocument document = new ReadReceiptDocument();
        document.setId(receipt.getId());
        document.setStudyGroupId(receipt.getStudyGroupId());
        document.setMessageId(receipt.getMessageId());
        document.setUserId(receipt.getUserId());
        document.setReadAt(receipt.getReadAt());
        return document;
    }
}

