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
    // Atributo de tipo String para almacenar token.
    private String token;
    // Atributo de tipo TipoUsuario para almacenar tipoUsuario.
    private TipoUsuario tipoUsuario;

    /**
     * Constructor de la clase LoginResponse con inyección de dependencias.
     *
     * @param token Parámetro de entrada para la operación.
     * @param tipoUsuario Parámetro de entrada para la operación.
     */
    public LoginResponse(String token, TipoUsuario tipoUsuario) {
        this.token = token;
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Recupera el valor actual de token.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getToken() { return token; }
    /**
     * Establece el valor de token.
     *
     * @param token Parámetro de entrada para la operación.
     */
    public void setToken(String token) { this.token = token; }
    /**
     * Recupera el valor actual de tipousuario.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    /**
     * Establece el valor de tipousuario.
     *
     * @param tipoUsuario Parámetro de entrada para la operación.
     */
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
