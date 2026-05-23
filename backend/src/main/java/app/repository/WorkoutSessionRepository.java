package app.repository;

import app.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para gestionar la persistencia de sesiones de entrenamiento.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByClientId(Long clientId);
    List<WorkoutSession> findByClientIdAndDate(Long clientId, LocalDate date);
    List<WorkoutSession> findByClientIdAndMuscleGroupOrderByDateAsc(Long clientId, String muscleGroup);
}
