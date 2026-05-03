package com.campusflow.domain.message.port;

import com.campusflow.domain.message.model.ReadReceipt;
import java.util.List;
import java.util.Optional;

public interface ReadReceiptRepositoryPort {
    ReadReceipt save(ReadReceipt receipt);

    Optional<ReadReceipt> findByMessageIdAndUserId(String messageId, Long userId);

    List<ReadReceipt> findByStudyGroupIdAndUserId(Long studyGroupId, Long userId);
}

