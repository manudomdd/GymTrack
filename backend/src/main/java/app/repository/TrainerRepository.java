package app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.Trainer;

/**
 * Repositorio JPA para realizar operaciones de persistencia en la entidad Trainer.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
	
	Optional<Trainer> findByTrainerCode(String trainerCode);
}
