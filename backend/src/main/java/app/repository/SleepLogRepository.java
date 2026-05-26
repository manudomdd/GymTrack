package app.repository;

import app.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la persistencia de registros de sueño.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     * @param clientId Identificador único del usuario o cliente asociado.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    List<SleepLog> findByClientId(Long clientId);
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     * @param clientId Identificador único del usuario o cliente asociado.
     * @param date Parámetro de entrada para la operación.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    Optional<SleepLog> findByClientIdAndDate(Long clientId, LocalDate date);
    
    void deleteAllByClientId(Long clientId);
}
