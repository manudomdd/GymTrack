package app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.Client;

/**
 * Repositorio JPA para realizar operaciones de persistencia en la entidad Client.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	
	/**
	 * Realiza una consulta para obtener los datos solicitados.
	 *
	 * @param trainerId Parámetro de entrada para la operación.
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	List<Client> findByTrainerId(Long trainerId);
}
