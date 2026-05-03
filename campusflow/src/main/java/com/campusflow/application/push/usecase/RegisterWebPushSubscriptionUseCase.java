package com.campusflow.application.push.usecase;

import com.campusflow.presentation.rest.push.PushSubscriptionRequest;

public interface RegisterWebPushSubscriptionUseCase {
    void register(Long userId, PushSubscriptionRequest subscriptionRequest);
}

