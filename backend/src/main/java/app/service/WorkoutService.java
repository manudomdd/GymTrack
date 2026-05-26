package app.service;

import app.entity.WorkoutSession;
import app.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los entrenamientos.
 * <p>
 * Proporciona métodos para registrar de forma masiva (batch) nuevas series, recuperar 
 * historiales optimizando las consultas SQL (evitando N+1), y ejecutar algoritmos
 * analíticos, como el cálculo de la regresión lineal para visualizar el progreso del 
 * cliente en base a la carga levantada a lo largo del tiempo.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Service
public class WorkoutService {


    @Autowired
    private WorkoutSessionRepository repository;

    public WorkoutSession saveSession(WorkoutSession session) {
        return repository.save(session);
    }

    /**
     * Persiste una lista completa de series de un mismo entrenamiento de una vez.
     * Cada elemento representa una serie individual con su número ordinal ya asignado.
     */
    public List<WorkoutSession> saveAllSessions(List<WorkoutSession> sessions) {
        return repository.saveAll(sessions);
    }

    public List<WorkoutSession> getSessionsByUser(Long userId) {
        return repository.findByClientId(userId);
    }

    /**
     * Carga todas las sesiones de una lista de clientes en una sola consulta SQL.
     * Elimina el problema N+1 del dashboard del entrenador.
     */
    public List<WorkoutSession> getSessionsByClientIds(List<Long> clientIds) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        return repository.findByClientIdIn(clientIds);
    }

    /** Busca una sesión por ID para validación de permisos antes de actualizarla. */
    public Optional<WorkoutSession> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Calcula la regresión lineal (pendiente) del progreso en las cargas (peso)
     * para cada grupo muscular del usuario.
     *
     * Correcciones aplicadas:
     * - Usa la query optimizada del repositorio (ya ordenada por fecha ASC).
     * - Solo incluye grupos musculares con al menos 2 fechas distintas para
     *   garantizar que la pendiente calculada tiene significado real.
     * - Grupos con datos insuficientes se omiten del mapa (no devuelven 0.0
     *   falso que confundiría con "estancado").
     */
    public Map<String, Double> calculateProgressMetrics(Long userId) {
        List<WorkoutSession> allSessions = repository.findByClientId(userId);

        // Agrupar por "MuscleGroup - Exercise"
        Map<String, List<WorkoutSession>> groupedSessions = allSessions.stream()
                .filter(s -> s.getMuscleGroup() != null && s.getExercise() != null && s.getPesoTotal() != null && s.getPesoTotal() > 0)
                .collect(Collectors.groupingBy(s -> s.getMuscleGroup() + " - " + s.getExercise()));

        Map<String, Double> metrics = new HashMap<>();

        for (Map.Entry<String, List<WorkoutSession>> entry : groupedSessions.entrySet()) {
            String key = entry.getKey();
            List<WorkoutSession> sessions = entry.getValue();

            // Ordenar por fecha ASC
            sessions.sort((a, b) -> a.getDate().compareTo(b.getDate()));

            if (sessions.size() < 2) continue;

            // Se requieren al menos 2 fechas distintas para calcular una tendencia
            long distinctDates = sessions.stream()
                    .map(WorkoutSession::getDate)
                    .distinct()
                    .count();
            if (distinctDates < 2) continue;

            WorkoutSession first = sessions.get(0);
            int n = sessions.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

            for (WorkoutSession s : sessions) {
                double x = ChronoUnit.DAYS.between(first.getDate(), s.getDate());
                double y = s.getPesoTotal();
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
            }

            double denominator = (n * sumX2) - (sumX * sumX);
            if (denominator == 0) continue; 

            double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
            metrics.put(key, slope);
        }

        return metrics;
    }

    public void deleteSession(Long id) {
        repository.deleteById(id);
    }

    public List<WorkoutSession> getSessionsByUserAndDate(Long userId, LocalDate date) {
        return repository.findByClientIdAndDate(userId, date);
    }
}
