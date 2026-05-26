package app.dto;

import app.entity.TipoUsuario;

/**
 * DTO para el listado de usuarios en el Panel de Administrador.
 */
public class UserAdminDTO {

    private Long id;
    private String nombre;
    private String username;
    private TipoUsuario tipoUsuario;

    public UserAdminDTO() {}

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
