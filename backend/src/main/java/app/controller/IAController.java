package app.controller;

import app.entity.User;
import app.repository.UserRepository;
import app.service.IAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainer")
public class IAController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private IAService iaService;

    @PostMapping("/client/{clientId}/ai-chat")
    public ResponseEntity<Map<String, String>> chatWithAI(
            Authentication auth,
            @PathVariable Long clientId,
            @RequestBody Map<String, String> request) {

        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            // Validar que el cliente esté vinculado al entrenador autenticado
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

    @PostMapping("/chat-general")
    public ResponseEntity<Map<String, String>> chatGeneral(
            Authentication auth,
            @RequestBody Map<String, String> request) {

        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
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
