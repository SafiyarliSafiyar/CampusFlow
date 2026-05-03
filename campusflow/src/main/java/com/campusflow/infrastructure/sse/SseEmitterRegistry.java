package com.campusflow.infrastructure.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRegistry {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void addEmitter(Long groupId, SseEmitter emitter) {
        emitters.computeIfAbsent(groupId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public void removeEmitter(Long groupId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(groupId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    public void broadcast(Long groupId, Object data) {
        List<SseEmitter> list = emitters.get(groupId);
        if (list == null || list.isEmpty()) {
            return;
        }

        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : list) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("message")
                                .data(data)
                );
            } catch (Exception _e) {
                dead.add(emitter);
            }
        }
        list.removeAll(dead);
    }
}

