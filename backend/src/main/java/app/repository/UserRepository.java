package app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.User;

/**
 * Repositorio JPA genérico para la entidad abstracta {@link User}.
 * <p>
 * Al extender de la clase base {@code User}, este repositorio permite realizar
 * consultas polimórficas. Es decir, puede buscar credenciales de acceso 
 * independientemente de si el usuario subyacente es un Cliente, un Entrenador
 * o un Administrador.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	/**
	 * Recupera un usuario por su nombre de usuario (username).
	 * <p>
	 * Spring Data JPA genera automáticamente la consulta SQL ({@code SELECT * FROM users WHERE username = ?})
	 * basándose exclusivamente en la convención de nomenclatura de este método.
	 * Se emplea exhaustivamente en el proceso de autenticación de Spring Security.
	 * </p>
	 * 
	 * @param username el nombre de usuario introducido en el login
	 * @return un {@link Optional} con el usuario si existe, o vacío si las credenciales son incorrectas
	 */

	Optional<User> findByUsername(String username); 

}