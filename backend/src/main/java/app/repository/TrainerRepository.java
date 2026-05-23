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
	
	/**
	 * Realiza una consulta para obtener los datos solicitados.
	 *
	 * @param trainerCode Parámetro de entrada para la operación.
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	Optional<Trainer> findByTrainerCode(String trainerCode);
}
