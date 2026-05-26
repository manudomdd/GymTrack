package app.dto;

/**
 * Objeto de transferencia de datos (DTO) utilizado para procesar 
 * las credenciales de inicio de sesión de un usuario.
 * <p>
 * Recibe el nombre de usuario (username) y la contraseña en texto plano
 * desde la aplicación cliente, los cuales serán validados por el sistema
 * de autenticación (Spring Security) en el backend.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class LoginRequest {

	
	private String username; 
	private String password;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	} 
}
