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
    List<SleepLog> findByClientId(Long clientId);
    Optional<SleepLog> findByClientIdAndDate(Long clientId, LocalDate date);
}
