package app.dto;

import app.entity.TipoUsuario;

/**
 * Objeto de transferencia de datos (DTO) que representa la respuesta exitosa
 * generada tras un inicio de sesión válido.
 * <p>
 * Devuelve el token JWT (JSON Web Token) asociado a la sesión del usuario, 
 * junto con su rol ({@link TipoUsuario}). Esto permite a la aplicación cliente 
 * almacenar el token para peticiones futuras y redirigir al usuario a su panel 
 * correspondiente (Cliente, Entrenador o Administrador).
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
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
