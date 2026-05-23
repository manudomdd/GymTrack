package app.dto;

import app.entity.TipoUsuario;

/**
 * DTO para la respuesta del proceso de inicio de sesión exitoso.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class LoginResponse {
    private String token;
    private TipoUsuario tipoUsuario;

    public LoginResponse(String token, TipoUsuario tipoUsuario) {
        this.token = token;
        this.tipoUsuario = tipoUsuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
