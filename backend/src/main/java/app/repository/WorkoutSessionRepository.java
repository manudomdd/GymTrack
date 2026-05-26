package app.repository;

import app.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la gestión de sesiones de entrenamiento.
 * <p>
 * Spring Data JPA genera automáticamente las implementaciones de todos
 * los métodos declarados a partir de su nombre, sin necesidad de escribir
 * consultas SQL de forma explícita.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    /**
     * Recupera todas las sesiones de entrenamiento de un cliente concreto.
     *
     * @param clientId identificador del cliente
     * @return lista de sesiones asociadas a ese cliente
     */
    List<WorkoutSession> findByClientId(Long clientId);

    /**
     * Recupera las sesiones de un cliente realizadas en una fecha específica.
     *
     * @param clientId identificador del cliente
     * @param date     fecha de las sesiones
     * @return lista de sesiones que coinciden con el cliente y la fecha
     */
    List<WorkoutSession> findByClientIdAndDate(Long clientId, LocalDate date);

    /**
     * Recupera las sesiones de un cliente filtradas por grupo muscular y
     * ordenadas cronológicamente de forma ascendente.
     *
     * @param clientId    identificador del cliente
     * @param muscleGroup nombre del grupo muscular
     * @return lista de sesiones ordenadas por fecha ascendente
     */
    List<WorkoutSession> findByClientIdAndMuscleGroupOrderByDateAsc(Long clientId, String muscleGroup);

    /**
     * Recupera en una única consulta SQL las sesiones de varios clientes a la vez.
     * Permite evitar el problema N+1 al cargar el dashboard del entrenador,
     * donde se necesitan los datos de múltiples clientes simultáneamente.
     *
     * @param clientIds lista de identificadores de clientes
     * @return lista de sesiones pertenecientes a alguno de los clientes indicados
     */
    List<WorkoutSession> findByClientIdIn(List<Long> clientIds);

    /**
     * Elimina todas las sesiones de entrenamiento asociadas a un cliente.
     * Se usa como paso previo a la eliminación del cliente para respetar
     * las restricciones de integridad referencial de la base de datos.
     *
     * @param clientId identificador del cliente cuyas sesiones se eliminarán
     */
    void deleteAllByClientId(Long clientId);
}
