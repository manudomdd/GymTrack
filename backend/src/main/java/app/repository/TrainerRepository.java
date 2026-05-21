package app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.Trainer;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
	
	Optional<Trainer> findByTrainerCode(String trainerCode);
}
