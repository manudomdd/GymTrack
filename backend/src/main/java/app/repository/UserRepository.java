package app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.entity.User;

/**
 * Repositorio JPA para realizar operaciones de persistencia en la entidad User.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	// Spring Data JPA es tan inteligente que solo con leer "findByUsername", 
	// ya sabe que tiene que hacer un "SELECT * FROM users WHERE username = ?" en MySQL.
	Optional<User> findByUsername(String username); 

}