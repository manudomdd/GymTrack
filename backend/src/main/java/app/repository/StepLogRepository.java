package app.repository;

import app.entity.StepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StepLogRepository extends JpaRepository<StepLog, Long> {
    List<StepLog> findByClientId(Long clientId);
    Optional<StepLog> findByClientIdAndDate(Long clientId, LocalDate date);
    void deleteAllByClientId(Long clientId);
}
