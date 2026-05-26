package app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Componente de configuración encargado de provisionar la cuenta de administrador
 * del sistema en el arranque de la aplicación.
 * <p>
 * Se ejecuta una única vez mediante {@link CommandLineRunner}. Si la cuenta ya existe
 * en base de datos, el proceso se omite sin lanzar ningún error.
 * </p>
 * <p>
 * <strong>Nota técnica:</strong> la inserción se realiza mediante {@link JdbcTemplate}
 * en lugar de JPA para evitar dos problemas específicos del entorno de producción:
 * <ul>
 *   <li>Hibernate 6 genera columnas {@code ENUM} reales en MySQL para los campos
 *       anotados con {@code @Enumerated(EnumType.STRING)}. Al añadir el valor {@code ADMIN}
 *       al enum de Java, {@code ddl-auto=update} no actualiza el tipo de la columna
 *       automáticamente, por lo que es necesario ejecutar un {@code ALTER TABLE} previo.</li>
 *   <li>La invocación de métodos {@code @Transactional} dentro del mismo bean
 *       (auto-invocación) no es interceptada por el proxy de Spring AOP, lo que
 *       haría que el {@code EntityManager} no tuviese transacción activa.</li>
 * </ul>
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    /**
     * Registra el runner que inicializa el usuario administrador.
     * <p>
     * El proceso sigue los siguientes pasos en orden:
     * <ol>
     *   <li>Actualiza la columna {@code tipo_usuario} de la tabla {@code users} para
     *       incluir {@code ADMIN} en el tipo ENUM de MySQL.</li>
     *   <li>Crea la tabla {@code admins} si no existe, requerida por la estrategia
     *       de herencia {@code InheritanceType.JOINED}.</li>
     *   <li>Inserta el usuario administrador en {@code users} con {@code INSERT IGNORE},
     *       de modo que la operación sea idempotente.</li>
     *   <li>Registra el identificador del administrador en la tabla {@code admins}.</li>
     * </ol>
     * </p>
     *
     * @param jdbcTemplate    cliente JDBC inyectado por Spring Boot
     * @param passwordEncoder codificador BCrypt para cifrar la contraseña
     * @return el runner que ejecuta la inicialización al arrancar el contexto
     */
    @Bean
    public CommandLineRunner seedAdmin(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            log.info(">>> AdminSeeder iniciando...");

            // Hibernate 6 mapea @Enumerated(STRING) como ENUM nativo de MySQL.
            // El comando ddl-auto=update no añade nuevos valores al ENUM existente,
            // por lo que hay que modificar la columna antes de insertar.
            jdbcTemplate.execute(
                "ALTER TABLE users MODIFY COLUMN tipo_usuario ENUM('CLIENTE', 'ENTRENADOR', 'ADMIN')"
            );
            log.info(">>> Columna tipo_usuario actualizada con ADMIN.");

            // La tabla admins es necesaria para que Hibernate pueda instanciar
            // la entidad Admin correctamente mediante InheritanceType.JOINED.
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS admins (id BIGINT NOT NULL, PRIMARY KEY (id))"
            );
            log.info(">>> Tabla admins lista.");

            // INSERT IGNORE garantiza idempotencia: si el usuario ya existe (constraint
            // UNIQUE sobre username), la sentencia no falla ni genera error.
            String pwd = passwordEncoder.encode("admin");
            jdbcTemplate.update(
                "INSERT IGNORE INTO users (nombre, username, password, tipo_usuario) VALUES (?, ?, ?, ?)",
                "Administrador Sistema", "admin", pwd, "ADMIN"
            );
            log.info(">>> INSERT en users ejecutado.");

            // Recuperar el ID asignado para registrar la fila en la tabla admins.
            Long adminId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = 'admin'",
                Long.class
            );
            log.info(">>> Admin id={}", adminId);

            // Registrar en admins con INSERT IGNORE para evitar duplicados
            // si el runner se ejecutase más de una vez.
            jdbcTemplate.update("INSERT IGNORE INTO admins (id) VALUES (?)", adminId);
            log.info(">>> AdminSeeder completado. Admin listo con id={}!", adminId);
        };
    }
}
