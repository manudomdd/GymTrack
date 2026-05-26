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
            try {
                // 1. Asegurar que la tabla admins existe (por si ddl-auto no la creó)
                jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS admins (" +
                    "  id BIGINT NOT NULL, " +
                    "  PRIMARY KEY (id), " +
                    "  CONSTRAINT fk_admins_users FOREIGN KEY (id) REFERENCES users(id)" +
                    ")"
                );

                // 2. Comprobar si ya existe en users
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users WHERE username = 'admin'",
                    Integer.class
                );

                if (count == null || count == 0) {
                    String encodedPassword = passwordEncoder.encode("admin");

                    // 3. Insertar en users
                    jdbcTemplate.update(
                        "INSERT INTO users (nombre, username, password, tipo_usuario) VALUES (?, ?, ?, ?)",
                        "Administrador Sistema", "admin", encodedPassword, "ADMIN"
                    );

                    // 4. Obtener el ID generado
                    Long adminId = jdbcTemplate.queryForObject(
                        "SELECT id FROM users WHERE username = 'admin'",
                        Long.class
                    );

                    // 5. Insertar en admins (requerido por JOINED inheritance)
                    jdbcTemplate.update("INSERT INTO admins (id) VALUES (?)", adminId);

                    log.info("✅ Admin creado correctamente con id={}", adminId);
                } else {
                    // 6. Existe en users, verificar que también esté en admins
                    Long adminId = jdbcTemplate.queryForObject(
                        "SELECT id FROM users WHERE username = 'admin'",
                        Long.class
                    );
                    Integer inAdmins = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM admins WHERE id = ?",
                        Integer.class, adminId
                    );
                    if (inAdmins == null || inAdmins == 0) {
                        jdbcTemplate.update("INSERT INTO admins (id) VALUES (?)", adminId);
                        log.info("✅ Fila en admins reparada para admin id={}", adminId);
                    } else {
                        log.info("ℹ️ Admin ya existe y está correcto (id={})", adminId);
                    }
                }
            } catch (Exception e) {
                log.error("❌ Error en AdminSeeder: {}", e.getMessage(), e);
            }
        };
    }
}
