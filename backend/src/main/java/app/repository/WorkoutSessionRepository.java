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
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     * @param clientId Identificador único del usuario o cliente asociado.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    List<WorkoutSession> findByClientId(Long clientId);
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     * @param clientId Identificador único del usuario o cliente asociado.
     * @param date Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    List<WorkoutSession> findByClientIdAndDate(Long clientId, LocalDate date);
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     * @param clientId Identificador único del usuario o cliente asociado.
     * @param muscleGroup Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    List<WorkoutSession> findByClientIdAndMuscleGroupOrderByDateAsc(Long clientId, String muscleGroup);
}
