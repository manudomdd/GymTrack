package app.config;

import app.entity.User;
import app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<User> adminOpt = userRepository.findByUsername("admin");
            if (adminOpt.isEmpty()) {
                insertAdmin(passwordEncoder.encode("admin"));
            } else {
                log.info("ℹ️ Usuario admin ya existe, se omite el seeder.");
            }
        };
    }

    @Transactional
    public void insertAdmin(String encodedPassword) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO users (nombre, username, password, tipo_usuario) " +
                "VALUES ('Administrador Sistema', 'admin', :pwd, 'ADMIN')"
            ).setParameter("pwd", encodedPassword).executeUpdate();
            log.info("✅ Usuario admin creado correctamente via SQL nativo.");
        } catch (Exception e) {
            log.error("❌ Error al crear usuario admin: {}", e.getMessage(), e);
        }
    }
}
