package app.config;

import app.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuración de Beans del sistema, incluyendo UserDetailsService y codificadores.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Configuration
public class ApplicationConfig {

    // Repositorio para operaciones de persistencia de la entidad User.
    private final UserRepository userRepository;

    /**
     * Constructor de la clase ApplicationConfig con inyección de dependencias.
     *
     * @param userRepository Parámetro de entrada para la operación.
     */
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * UserDetailsService que carga el usuario por email.
     * @Transactional garantiza que la sesión de Hibernate está abierta durante
     * la carga, evitando LazyInitializationException al acceder a getAuthorities().
     */
    @Bean
    @Transactional
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
