package com.campusflow.infrastructure.persistence.mongo.readreceipt;

import com.campusflow.domain.message.model.ReadReceipt;
import com.campusflow.domain.message.port.ReadReceiptRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReadReceiptMongoAdapter implements ReadReceiptRepositoryPort {
    private final ReadReceiptMongoRepository readReceiptMongoRepository;

    @Override
    public ReadReceipt save(ReadReceipt receipt) {
        ReadReceiptDocument saved = readReceiptMongoRepository.save(ReadReceiptDocument.fromDomain(receipt));
        return saved.toDomain();
    }

    @Override
    public Optional<ReadReceipt> findByMessageIdAndUserId(String messageId, Long userId) {
        return readReceiptMongoRepository.findByMessageIdAndUserId(messageId, userId).map(ReadReceiptDocument::toDomain);
    }

    @Override
    public List<ReadReceipt> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId) {
        return readReceiptMongoRepository.findByStudyGroupIdAndUserId(studyGroupId, userId)
                .stream()
                .map(ReadReceiptDocument::toDomain)
                .toList();
    }
}

