package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para la gestión de notificaciones en tiempo real a través de Server-Sent Events (SSE).
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Service
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Procesa la operación correspondiente para subscribe.
     *
     * @param userId Identificador único del usuario o cliente asociado.
     * @return Canal de flujo de eventos persistentes (Server-Sent Events).
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Conexión de notificaciones establecida con éxito"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * Procesa la operación correspondiente para sendNotification.
     *
     * @param userId Identificador único del usuario o cliente asociado.
     * @param message Parámetro de entrada para la operación.
     */
    public void sendNotification(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("comment")
                        .data(message));
            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
