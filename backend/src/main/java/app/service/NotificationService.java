package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    // Almacena los emisores activos mapeados por el ID de usuario
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Registra un cliente para recibir Server-Sent Events.
     */
    public SseEmitter subscribe(Long userId) {
        // Creamos un emisor con un timeout largo (24 horas)
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);

        // Limpiar el mapa ante eventos de ciclo de vida del emitter
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);

        // Envía un evento inicial de conexión exitosa
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
     * Envía una notificación en tiempo real a un usuario específico si está conectado.
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
