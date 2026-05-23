package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        // Instanciamos con timeout de 24 horas
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);

        // Mantenimiento estricto del ciclo de vida del emisor
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(userId);
        });
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Conexión de notificaciones establecida con éxito"));
        } catch (Exception e) {
            emitters.remove(userId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendNotification(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("comment")
                        .data(message));
            } catch (Exception e) {
                // Si el emisor falló (el cliente cerró la app, cortó internet, etc.), lo purgamos
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
