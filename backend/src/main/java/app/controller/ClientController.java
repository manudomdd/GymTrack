package app.controller;

import app.entity.SleepLog;
import app.entity.StepLog;
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
import java.util.List;
import java.util.Optional;
import app.dto.DashboardClientDTO;
import app.service.NotificationService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private HealthService healthService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication auth) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Metodo mediante el cual un cliente podrá actualizar los datos de su perfil.
     * @param auth
     * @param updateData
     * @return
     */
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(Authentication auth, @RequestBody User updateData) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNombre(updateData.getNombre());
            user.setPeso(updateData.getPeso());
            user.setAltura(updateData.getAltura());

            // Solo actualizamos NEAT si viene un valor distinto de cero (o si se desea mantener).
            if (updateData.getNeat() > 0) {
                user.setNeat(updateData.getNeat());
            }

            return ResponseEntity.ok(userRepo.save(user));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Enpoint el cual sirve para listar los entrenamientos. 
     * @param auth
     * @return
     */
    @GetMapping("/workouts")
    public ResponseEntity<List<WorkoutSession>> getWorkouts(Authentication auth) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(workoutService.getSessionsByUser(userOpt.get().getId()));
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Endpoint con metodo para añadir un nuevo ejercicio al entrenamiento.
     * @param auth
     * @param session
     * @return
     */
    @PostMapping("/workouts")
    public ResponseEntity<WorkoutSession> addWorkout(Authentication auth, @RequestBody WorkoutSession session) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            session.setUser(userOpt.get());
            return ResponseEntity.ok(workoutService.saveSession(session));
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Endpoint batch: recibe la lista completa de series de un entrenamiento
     * (cada elemento es una serie individual con su número ordinal).
     * Es el endpoint principal usado por el cliente para guardar su sesión.
     *
     * @param auth     usuario autenticado
     * @param sessions lista de series a persistir
     * @return lista de series guardadas
     */
    @PostMapping("/workouts/batch")
    public ResponseEntity<List<WorkoutSession>> addWorkoutBatch(Authentication auth,
            @RequestBody List<WorkoutSession> sessions) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userOpt.get();
        sessions.forEach(s -> s.setUser(user));
        return ResponseEntity.ok(workoutService.saveAllSessions(sessions));
    }

    /**
     * Permite al cliente actualizar peso/reps/rir de una serie ya registrada.
     * Solo el propietario de la sesión puede actualizarla.
     *
     * @param auth   usuario autenticado
     * @param id     ID de la serie a actualizar
     * @param update objeto con los campos a actualizar
     * @return serie actualizada o 403/404 si no procede
     */
    @PutMapping("/workouts/{id}")
    public ResponseEntity<?> updateWorkoutExecution(Authentication auth,
            @PathVariable Long id, @RequestBody WorkoutSession update) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Optional<WorkoutSession> sessionOpt = workoutService.findById(id);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WorkoutSession session = sessionOpt.get();
        if (!session.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }
        session.setReps(update.getReps());
        session.setPesoTotal(update.getPesoTotal());
        session.setRir(update.getRir());
        return ResponseEntity.ok(workoutService.saveSession(session));
    }

    /**
     * Permite al cliente eliminar una serie concreta por su ID.
     * Solo el propietario de la serie puede eliminarla.
     */
    @DeleteMapping("/workouts/{id}")
    public ResponseEntity<?> deleteWorkoutSession(Authentication auth, @PathVariable Long id) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Optional<WorkoutSession> sessionOpt = workoutService.findById(id);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WorkoutSession session = sessionOpt.get();
        if (!session.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }
        workoutService.deleteSession(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Metodo para añadir un registro de sueño (numero de 1 a 10 calidad de sueño, con numero de horas dormidas).
     * @param auth
     * @param log
     * @return
     */
    @PostMapping("/health/sleep")
    public ResponseEntity<SleepLog> addSleepLog(Authentication auth, @RequestBody SleepLog log) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            log.setUser(userOpt.get());
            return ResponseEntity.ok(healthService.saveSleepLog(log));
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Metodo para añadir un nuevo registro de pasos. 
     * @param auth
     * @param log
     * @return
     */
    @PostMapping("/health/steps")
    public ResponseEntity<StepLog> addStepLog(Authentication auth, @RequestBody StepLog log) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            log.setUser(userOpt.get());
            return ResponseEntity.ok(healthService.saveStepLog(log));
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Metodo para enlazar un cliente con su correspondiente entrenador. 
     * @param auth
     * @param code
     * @return
     */
    @PostMapping("/link-trainer/{code}")
    public ResponseEntity<String> linkTrainer(Authentication auth, @PathVariable String code) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            User client = userOpt.get();
            Optional<User> trainerOpt = userRepo.findByTrainerCode(code);

            if (trainerOpt.isPresent() && trainerOpt.get().getTipoUsuario() == app.entity.TipoUsuario.ENTRENADOR) {
                client.setTrainer(trainerOpt.get());
                userRepo.save(client);
                return ResponseEntity.ok("Entrenador vinculado con éxito");
            } else {
                return ResponseEntity.badRequest().body("Código de entrenador inválido");
            }
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Endpoint para obtener los datos del Dashboard del Cliente
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardClientDTO> getDashboard(Authentication auth) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Long userId = user.getId();
            
            // Entrenamientos: count distinct dates in WorkoutSession
            List<WorkoutSession> sessions = workoutService.getSessionsByUser(userId);
            long entrenamientos = sessions.stream().map(WorkoutSession::getDate).distinct().count();
            
            // Pasos de hoy
            LocalDate hoy = LocalDate.now();
            List<StepLog> steps = healthService.getStepLogs(userId);
            int pasosHoy = steps.stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(hoy))
                .mapToInt(StepLog::getSteps)
                .sum();
            
            // Calorias (mocked as pasos * 0.04)
            int calorias = (int) (pasosHoy * 0.04);
            
            // Horas de sueño hoy
            List<SleepLog> sleeps = healthService.getSleepLogs(userId);
            double horasSueno = sleeps.stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(hoy))
                .mapToDouble(SleepLog::getHoursSlept)
                .sum();
                
            DashboardClientDTO dto = new DashboardClientDTO((int) entrenamientos, pasosHoy, calorias, horasSueno);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping(value = "/notifications/sse", produces = "text/event-stream")
    public SseEmitter subscribeToNotifications(Authentication auth) {
        Optional<User> userOpt = userRepo.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            return notificationService.subscribe(userOpt.get().getId());
        }
        return null;
    }
}
