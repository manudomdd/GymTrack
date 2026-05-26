package app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    public CommandLineRunner seedAdmin(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            log.info(">>> AdminSeeder iniciando...");

            // Crear tabla admins sin FK (evita conflictos con constraints existentes)
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS admins (id BIGINT NOT NULL, PRIMARY KEY (id))"
            );
            log.info(">>> Tabla admins lista.");

            // INSERT IGNORE: no falla si ya existe (unique constraint en username)
            String pwd = passwordEncoder.encode("admin");
            jdbcTemplate.update(
                "INSERT IGNORE INTO users (nombre, username, password, tipo_usuario) VALUES (?, ?, ?, ?)",
                "Administrador Sistema", "admin", pwd, "ADMIN"
            );
            log.info(">>> INSERT en users ejecutado.");

            // Obtener ID del admin
            Long adminId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = 'admin'",
                Long.class
            );
            log.info(">>> Admin id={}", adminId);

            // INSERT IGNORE en admins
            jdbcTemplate.update("INSERT IGNORE INTO admins (id) VALUES (?)", adminId);
            log.info(">>> INSERT en admins ejecutado. Admin listo!");
        };
    }
}
