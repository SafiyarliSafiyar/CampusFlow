package com.campusflow.infrastructure.persistence.mongo.message;

import com.campusflow.domain.message.model.Message;
import com.campusflow.domain.message.port.MessageRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageMongoAdapter implements MessageRepositoryPort {
    private final MessageMongoRepository messageMongoRepository;

    @Override
    public Message save(Message message) {
        MessageDocument saved = messageMongoRepository.save(MessageDocument.fromDomain(message));
        return saved.toDomain();
    }

    @Override
    public List<Message> findByStudyGroupId(Long studyGroupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageMongoRepository.findByStudyGroupIdOrderBySentAtDesc(studyGroupId, pageable)
                .stream()
                .map(MessageDocument::toDomain)
                .toList();
    }

    @Override
    public long countByStudyGroupId(Long studyGroupId) {
        return messageMongoRepository.countByStudyGroupId(studyGroupId);
    }
}
