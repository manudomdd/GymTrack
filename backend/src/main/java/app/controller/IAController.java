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
 * Controlador REST para la comunicación con los servicios de inteligencia artificial.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@RestController
@RequestMapping("/api/trainer")
public class IAController {

    // Repositorio para operaciones de persistencia de la entidad User.
    @Autowired
    private UserRepository userRepo;

    // Repositorio para operaciones de persistencia de la entidad Client.
    @Autowired
    private ClientRepository clientRepo;

    // Servicio para la gestión de la lógica de negocio de Ia.
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
