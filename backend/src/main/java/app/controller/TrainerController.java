package app.controller;

import app.entity.User;
import app.entity.WorkoutSession;
import app.repository.UserRepository;
import app.service.HealthService;
import app.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import app.dto.DashboardTrainerDTO;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private HealthService healthService;

    /**
     * Obtiene los clientes asignados a un entrenador determinado.
     */
    @GetMapping("/clients")
    public ResponseEntity<List<app.dto.ClientSummaryDTO>> getClients(Authentication auth) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        if (trainerOpt.isPresent()) {
            List<User> clients = userRepo.findByTrainerId(trainerOpt.get().getId());
            List<app.dto.ClientSummaryDTO> dtoList = clients.stream().map(client -> {
                String ultimoGrupo = "Ninguno";
                try {
                    List<WorkoutSession> sessions = workoutService.getSessionsByUser(client.getId());
                    if (sessions != null && !sessions.isEmpty()) {
                        LocalDate lastWorkout = sessions.stream()
                                .map(WorkoutSession::getDate)
                                .filter(d -> d != null)
                                .max(LocalDate::compareTo)
                                .orElse(null);

                        if (lastWorkout != null) {
                            java.util.List<String> groups = sessions.stream()
                                    .filter(s -> lastWorkout.equals(s.getDate()))
                                    .map(WorkoutSession::getMuscleGroup)
                                    .filter(g -> g != null && !g.trim().isEmpty())
                                    .distinct()
                                    .collect(java.util.stream.Collectors.toList());
                            if (!groups.isEmpty()) {
                                ultimoGrupo = String.join(", ", groups);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ultimoGrupo = "Error";
                }

                return new app.dto.ClientSummaryDTO(
                        client.getId(),
                        client.getNombre() != null ? client.getNombre() : "—",
                        client.getEmail() != null ? client.getEmail() : "—",
                        client.getPeso(),
                        client.getAltura(),
                        client.getEdad(),
                        ultimoGrupo
                );
            }).collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(dtoList);
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Asigna un cliente a un entrenador.
     */
    @PostMapping("/assignClient/{clientId}")
    public ResponseEntity<String> assignClient(Authentication auth, @PathVariable Long clientId) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            client.setTrainer(trainerOpt.get());
            userRepo.save(client);
            return ResponseEntity.ok("Cliente asignado exitosamente.");
        }
        return ResponseEntity.badRequest().body("Entrenador o Cliente no encontrados.");
    }

    /**
     * Obtiene las métricas de progreso (regresión lineal sobre peso) de un cliente.
     * El cliente debe estar vinculado al entrenador autenticado.
     */
    @GetMapping("/client/{clientId}/progress")
    public ResponseEntity<Map<String, Double>> getClientProgress(
            Authentication auth, @PathVariable Long clientId) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            if (client.getTrainer() != null
                    && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                return ResponseEntity.ok(workoutService.calculateProgressMetrics(clientId));
            }
        }
        return ResponseEntity.status(403).build();
    }

    /**
     * Asigna una sesión de entrenamiento a un cliente del entrenador autenticado.
     * El cliente debe estar vinculado al entrenador; de lo contrario se devuelve 403.
     *
     * @param auth     autenticación del entrenador
     * @param clientId ID del cliente destinatario
     * @param session  datos de la sesión a asignar (incluye plannedSets)
     */
    @PostMapping("/client/{clientId}/workouts")
    public ResponseEntity<WorkoutSession> assignWorkout(Authentication auth,
            @PathVariable Long clientId, @RequestBody WorkoutSession session) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            if (client.getTrainer() != null
                    && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                session.setUser(client);
                return ResponseEntity.ok(workoutService.saveSession(session));
            }
        }
        return ResponseEntity.status(403).build();
    }

    /**
     * Devuelve los registros de salud (sueño y pasos) de un cliente vinculado
     * al entrenador autenticado. Permite al entrenador cruzar los datos de bienestar
     * con el rendimiento en los entrenamientos.
     *
     * @param auth     autenticación del entrenador
     * @param clientId ID del cliente a consultar
     * @return mapa con "sleepLogs" y "stepLogs" del cliente
     */
    @GetMapping("/client/{clientId}/health")
    public ResponseEntity<Map<String, Object>> getClientHealth(
            Authentication auth, @PathVariable Long clientId) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            if (client.getTrainer() != null
                    && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                Map<String, Object> health = new HashMap<>();
                health.put("sleepLogs", healthService.getSleepLogs(clientId));
                health.put("stepLogs", healthService.getStepLogs(clientId));
                return ResponseEntity.ok(health);
            }
        }
        return ResponseEntity.status(403).build();
    }

    /**
     * Obtiene los entrenamientos de un cliente vinculado al entrenador autenticado.
     * Permite visualizar el diario de entrenamiento del cliente en tiempo real.
     */
    @GetMapping("/client/{clientId}/workouts")
    public ResponseEntity<List<WorkoutSession>> getClientWorkouts(
            Authentication auth, @PathVariable Long clientId) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            if (client.getTrainer() != null
                    && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                return ResponseEntity.ok(workoutService.getSessionsByUser(clientId));
            }
        }
        return ResponseEntity.status(403).build();
    }

    /**
     * Obtiene los datos para el Dashboard del entrenador.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardTrainerDTO> getDashboard(Authentication auth) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        if (trainerOpt.isPresent()) {
            User trainer = trainerOpt.get();
            List<User> clients = userRepo.findByTrainerId(trainer.getId());
            
            int clientesTotales = clients.size();
            int activosHoy = 0;
            int entrenamientos = 0;
            int estaSemana = 0;
            
            LocalDate hoy = LocalDate.now();
            
            for (User client : clients) {
                List<WorkoutSession> sessions = workoutService.getSessionsByUser(client.getId());
                long distinctDates = sessions.stream().filter(s -> s.getDate() != null).map(WorkoutSession::getDate).distinct().count();
                entrenamientos += distinctDates;
                
                boolean isActivoHoy = sessions.stream().anyMatch(s -> s.getDate() != null && s.getDate().equals(hoy));
                if (isActivoHoy) activosHoy++;
                
                long workoutsThisWeek = sessions.stream()
                    .filter(s -> s.getDate() != null)
                    .map(WorkoutSession::getDate)
                    .distinct()
                    .filter(d -> ChronoUnit.DAYS.between(d, hoy) <= 7 && ChronoUnit.DAYS.between(d, hoy) >= 0)
                    .count();
                estaSemana += workoutsThisWeek;
            }
            
            DashboardTrainerDTO dto = new DashboardTrainerDTO(clientesTotales, activosHoy, entrenamientos, estaSemana);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Guarda o actualiza el feedback del entrenador para un cliente en un día determinado.
     */
    @PutMapping("/client/{clientId}/feedback")
    public ResponseEntity<?> updateClientFeedback(
            Authentication auth,
            @PathVariable Long clientId,
            @RequestParam("date") String dateStr,
            @RequestBody Map<String, String> body) {
        Optional<User> trainerOpt = userRepo.findByEmail(auth.getName());
        Optional<User> clientOpt = userRepo.findById(clientId);

        if (trainerOpt.isPresent() && clientOpt.isPresent()) {
            User client = clientOpt.get();
            if (client.getTrainer() != null && client.getTrainer().getId().equals(trainerOpt.get().getId())) {
                LocalDate date = LocalDate.parse(dateStr);
                String feedback = body.get("feedback");

                List<WorkoutSession> sessions = workoutService.getSessionsByUserAndDate(clientId, date);
                if (sessions.isEmpty()) {
                    return ResponseEntity.badRequest().body("No hay series registradas en esta fecha para dejar feedback.");
                }
                for (WorkoutSession session : sessions) {
                    session.setFeedbackEntrenador(feedback);
                    workoutService.saveSession(session);
                }
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(403).build();
    }
}
