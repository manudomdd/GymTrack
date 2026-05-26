package app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.Trainer;

/**
 * Repositorio JPA para la gestión de los datos de la entidad {@link Trainer}.
 * <p>
 * Proporciona métodos estándar de persistencia y consultas personalizadas
 * necesarias para la lógica de vinculación de usuarios.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * Busca un entrenador en la base de datos utilizando su código único.
     * <p>
     * Este método se utiliza principalmente durante el proceso de registro de un cliente
     * para asociarlo a su entrenador correspondiente mediante el código proporcionado.
     * </p>
     *
     * @param trainerCode el código identificativo único del entrenador (ej. TR-A1B2C3)
     * @return un {@link Optional} que contiene el entrenador si se encuentra, o vacío si no
     */

	
	Optional<Trainer> findByTrainerCode(String trainerCode);
}
