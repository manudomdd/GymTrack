package app.controller;

import app.entity.User;
import app.repository.UserRepository;
import app.repository.ClientRepository;
import app.service.IAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST que provee la integración con el motor de Inteligencia Artificial.
 * <p>
 * Expone endpoints exclusivos para los entrenadores que permiten generar respuestas
 * automatizadas. Proporciona tanto un chat general para consultas genéricas de fitness,
 * como un chat contextualizado que tiene en cuenta las métricas y el progreso
 * específico de un cliente vinculado para ofrecer recomendaciones personalizadas.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@RestController
@RequestMapping("/api/trainer")
public class IAController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private IAService iaService;

    /**
     * Endpoint para iniciar un chat especifico con la IA.
     * @param auth Entrenador autenticado.
     * @param clientId ID del cliente en concreto.
     * @param request Mensaje enviado por el entrenador.
     * @return Respuesta de la IA en formato JSON.
     */
    @PostMapping("/client/{clientId}/ai-chat")
    public ResponseEntity<Map<String, String>> chatWithAI(
            Authentication auth,
            @PathVariable Long clientId,
            @RequestBody Map<String, String> request) {

        Optional<User> trainerOpt = userRepo.findByUsername(auth.getName());
        Optional<app.entity.Client> clientOpt = clientRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            app.entity.Client client = clientOpt.get();
            // Comprobación de que el cliente está vinculado al entrenador autenticado.
            if (client.getTrainer() != null
                    && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                
                String trainerQuery = request.getOrDefault("message", "");
                if (trainerQuery.trim().isEmpty()) {
                    Map<String, String> err = new HashMap<>();
                    err.put("error", "El mensaje no puede estar vacío.");
                    return ResponseEntity.badRequest().body(err);
                }

                String aiResponse = iaService.generateAIResponse(clientId, trainerQuery);
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("response", aiResponse);
                return ResponseEntity.ok(responseMap);
            }
        }
        return ResponseEntity.status(403).build();
    }

    /**
     * Endpoint especifico para iniciar un chat general con la IA.
     * @param auth Entrenador autenticado
     * @param request Mensaje enviado a la IA.
     * @return Respuesta de la IA en formato JSON.
     */
    @PostMapping("/chat-general")
    public ResponseEntity<Map<String, String>> chatGeneral(
            Authentication auth,
            @RequestBody Map<String, String> request) {

        Optional<User> trainerOpt = userRepo.findByUsername(auth.getName());
        if (trainerOpt.isPresent()) {
            String trainerQuery = request.getOrDefault("message", "");
            if (trainerQuery.trim().isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "El mensaje no puede estar vacío.");
                return ResponseEntity.badRequest().body(err);
            }

            String aiResponse = iaService.generateGeneralAIResponse(trainerQuery);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("response", aiResponse);
            return ResponseEntity.ok(responseMap);
        }
        return ResponseEntity.status(403).build();
    }
}
