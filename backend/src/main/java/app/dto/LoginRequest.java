package app.dto;

/**
 * DTO que contiene las credenciales de inicio de sesión del usuario.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class LoginRequest {

	// Nombre de usuario único (dirección de correo electrónico).
	private String username;
	// Contraseña encriptada para el acceso seguro al sistema.
	private String password;

	/**
	 * Recupera el valor actual de username.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public String getUsername() {
		return username;
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
}
