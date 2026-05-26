package app.dto;

import app.entity.TipoUsuario;

/**
 * Objeto de transferencia de datos utilizado por el panel de administración
 * para listar los usuarios del sistema.
 * <p>
 * Expone únicamente los campos necesarios para la vista de gestión de usuarios,
 * evitando serializar información sensible como la contraseña cifrada.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class UserAdminDTO {

    /** Identificador único del usuario en base de datos. */
    private Long id;

    /** Nombre completo o alias del usuario. */
    private String nombre;

    /** Nombre de usuario empleado para el inicio de sesión. */
    private String username;

    /** Rol asignado al usuario dentro del sistema. */
    private TipoUsuario tipoUsuario;

    public UserAdminDTO() {}

    /**
     * Constructor que inicializa todos los campos del DTO.
     *
     * @param id          identificador del usuario
     * @param nombre      nombre completo del usuario
     * @param username    nombre de usuario para el login
     * @param tipoUsuario rol del usuario ({@code CLIENTE} o {@code ENTRENADOR})
     */
    public UserAdminDTO(Long id, String nombre, String username, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
