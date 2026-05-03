package com.campusflow.domain.message.port;

import com.campusflow.domain.message.model.Message;
import java.util.List;

public interface MessageRepositoryPort {
    Message save(Message message);

    List<Message> findByStudyGroupId(Long studyGroupId, int page, int size);

    long countByStudyGroupId(Long studyGroupId);
}
