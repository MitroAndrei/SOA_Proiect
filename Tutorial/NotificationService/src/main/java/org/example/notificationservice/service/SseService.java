package org.example.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.model.OrderCreatedEvent;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseService {
    // userId -> list of SSE connections (multiple devices)
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(0L); // No timeout

        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        log.info("Device connected for user: {} (total: {})",
                userId, emitters.get(userId).size());

        return emitter;
    }

    public void sendToUser(String userId, OrderCreatedEvent event) {
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) {
            log.debug("No connected devices for user: {}", userId);
            return;
        }

        userEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("ORDER_CREATED")
                        .data(event, MediaType.APPLICATION_JSON));
                log.info("Pushed order {} to device of user: {}",
                        event.getOrderId(), userId);
            } catch (Exception e) {
                remove(userId, emitter);
            }
        });
    }

    private void remove(String userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            log.info("Device disconnected for user: {} (remaining: {})",
                    userId, userEmitters.size());
        }
    }
}
