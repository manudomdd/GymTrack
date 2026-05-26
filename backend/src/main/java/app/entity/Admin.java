package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidad que representa a un usuario con el rol de Administrador en la aplicación.
 * Se almacena en la tabla 'users' como los demás usuarios, diferenciado por tipoUsuario=ADMIN.
 */
@Entity
@Table(name = "admins")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin extends User {

    public Admin() {
        super();
    }
}
