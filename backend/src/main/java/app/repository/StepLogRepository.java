package app.repository;

import app.entity.StepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la gestión de los registros de pasos diarios de los clientes.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Repository
public interface StepLogRepository extends JpaRepository<StepLog, Long> {

    /**
     * Recupera todos los registros de pasos de un cliente.
     *
     * @param clientId identificador del cliente
     * @return lista de registros de pasos asociados a ese cliente
     */
    List<StepLog> findByClientId(Long clientId);

    /**
     * Recupera el registro de pasos de un cliente en una fecha concreta.
     * Devuelve un Optional vacío si no existe registro para esa fecha.
     *
     * @param clientId identificador del cliente
     * @param date     fecha del registro
     * @return Optional con el registro si existe, vacío en caso contrario
     */
    Optional<StepLog> findByClientIdAndDate(Long clientId, LocalDate date);

    /**
     * Elimina todos los registros de pasos de un cliente.
     * Se invoca antes de eliminar la cuenta del cliente para mantener
     * la integridad referencial de la base de datos.
     *
     * @param clientId identificador del cliente cuyos registros se eliminarán
     */
    void deleteAllByClientId(Long clientId);
}
