package app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.Client;

/**
 * Repositorio JPA para la gestión de los datos de la entidad {@link Client}.
 * <p>
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar) sobre la tabla 
 * {@code clients} y define consultas personalizadas, como la búsqueda de clientes 
 * por su entrenador asignado.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Recupera todos los clientes que están vinculados a un entrenador específico.
     *
     * @param trainerId el identificador único del entrenador
     * @return lista de clientes asignados a ese entrenador
     */

	
	List<Client> findByTrainerId(Long trainerId);
}
