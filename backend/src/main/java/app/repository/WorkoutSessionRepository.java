package app.repository;

import app.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByClientId(Long clientId);
    List<WorkoutSession> findByClientIdAndDate(Long clientId, LocalDate date);
    List<WorkoutSession> findByClientIdAndMuscleGroupOrderByDateAsc(Long clientId, String muscleGroup);
    // Query batch: carga todas las sesiones de una lista de clientes en una sola consulta SQL (evita el problema N+1)
    List<WorkoutSession> findByClientIdIn(List<Long> clientIds);
    void deleteAllByClientId(Long clientId);
}
