package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa al usuario administrador del sistema.
 * <p>
 * Hereda todos los campos comunes de {@link User} mediante la estrategia de herencia
 * {@code InheritanceType.JOINED}, por lo que sus datos base se almacenan en la tabla
 * {@code users} y el registro identificador en la tabla {@code admins}.
 * </p>
 * <p>
 * A diferencia de los roles {@code CLIENTE} y {@code ENTRENADOR}, el administrador
 * no puede registrarse mediante el flujo público; su cuenta se provisiona
 * automáticamente al arrancar el backend a través de {@link app.config.AdminSeeder}.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 * @see User
 * @see app.config.AdminSeeder
 */
@Entity
@Table(name = "admins")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin extends User {

    public Admin() {
        super();
    }
}
