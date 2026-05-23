package app.entity;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Clase abstracta base que representa un usuario del sistema para Spring Security.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table (name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class User implements UserDetails {

    // Identificador único autoincremental de la entidad.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nombre completo del usuario.
    private String nombre;
    
    // Nombre de usuario único (dirección de correo electrónico).
    @jakarta.persistence.Column(unique = true, nullable = false)
    private String username;
    
    // Contraseña encriptada para el acceso seguro al sistema.
    private String password;
    
    // Atributo de tipo TipoUsuario para almacenar tipoUsuario.
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario; 
    
    // Nombre de archivo o URL del avatar del usuario.
    private String avatar;
    
    /**
     * Constructor de la clase User con inyección de dependencias.
     *
     */
    public User() {
        super();
    }

    /**
     * Constructor de la clase User con inyección de dependencias.
     *
     * @param id Parámetro de entrada para la operación.
     * @param nombre Parámetro de entrada para la operación.
     * @param username Parámetro de entrada para la operación.
     * @param password Parámetro de entrada para la operación.
     */
    public User(Long id, String nombre, String username, String password) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
    }

    /**
     * Recupera el valor actual de id.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el valor de id.
     *
     * @param id Parámetro de entrada para la operación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Recupera el valor actual de nombre.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el valor de nombre.
     *
     * @param nombre Parámetro de entrada para la operación.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Recupera el valor actual de username.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Establece el valor de username.
     *
     * @param username Parámetro de entrada para la operación.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Recupera el valor actual de password.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece el valor de password.
     *
     * @param password Parámetro de entrada para la operación.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = (tipoUsuario != null)
                ? "ROLE_" + tipoUsuario.name()
                : "ROLE_CLIENTE";
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * Recupera el valor actual de tipousuario.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    /**
     * Establece el valor de tipousuario.
     *
     * @param tipoUsuario Parámetro de entrada para la operación.
     */
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Recupera el valor actual de avatar.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Establece el valor de avatar.
     *
     * @param avatar Parámetro de entrada para la operación.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
