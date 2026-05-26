package app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigInteger;
import java.util.List;

@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public CommandLineRunner seedAdmin(PasswordEncoder passwordEncoder) {
        return args -> createAdminIfNeeded(passwordEncoder.encode("admin"));
    }

    @Transactional
    public void createAdminIfNeeded(String encodedPassword) {
        try {
            // 1. Buscar si ya existe en users
            List<?> rows = entityManager.createNativeQuery(
                "SELECT id FROM users WHERE username = 'admin'"
            ).getResultList();

            Long userId = null;

            if (rows.isEmpty()) {
                // 2a. No existe en users → insertar en users
                entityManager.createNativeQuery(
                    "INSERT INTO users (nombre, username, password, tipo_usuario) " +
                    "VALUES ('Administrador Sistema', 'admin', :pwd, 'ADMIN')"
                ).setParameter("pwd", encodedPassword).executeUpdate();

                // Obtener el ID generado
                Object idResult = entityManager.createNativeQuery(
                    "SELECT LAST_INSERT_ID()"
                ).getSingleResult();
                userId = ((BigInteger) idResult).longValue();
                log.info("✅ Fila en 'users' creada para admin con id={}", userId);
            } else {
                // 2b. Ya existe en users → obtener su ID
                Object idResult = rows.get(0);
                userId = ((BigInteger) idResult).longValue();
                log.info("ℹ️ Admin ya existe en 'users' con id={}", userId);
            }

            // 3. Verificar si existe en admins (necesario para JOINED inheritance)
            List<?> adminRows = entityManager.createNativeQuery(
                "SELECT id FROM admins WHERE id = :id"
            ).setParameter("id", userId).getResultList();

            if (adminRows.isEmpty()) {
                entityManager.createNativeQuery(
                    "INSERT INTO admins (id) VALUES (:id)"
                ).setParameter("id", userId).executeUpdate();
                log.info("✅ Fila en 'admins' creada para id={} — admin listo.", userId);
            } else {
                log.info("ℹ️ Admin ya tiene fila en 'admins', nada que hacer.");
            }

        } catch (Exception e) {
            log.error("❌ Error al crear usuario admin: {}", e.getMessage(), e);
        }
    }
}
