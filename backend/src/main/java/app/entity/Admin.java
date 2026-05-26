package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Entidad que representa a un usuario con el rol de Administrador en la aplicación.
 */
@Entity
@Table(name = "admins")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin extends User {

    public Admin() {
        super();
    }
}
