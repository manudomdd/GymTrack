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
	
	List<Client> findByTrainerId(Long trainerId);
}
