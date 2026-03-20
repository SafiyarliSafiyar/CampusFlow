package com.campusflow.application.studygroup.usecase;

public interface RemoveMemberUseCase {
    void removeMember(Long studyGroupId, Long memberId, Long actorId);
}
