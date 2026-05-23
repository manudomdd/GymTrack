package app.dto;

import app.entity.TipoUsuario;

/**
 * DTO que encapsula los datos para el registro de nuevos usuarios.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class RegisterRequest {
	
	// Nombre completo del usuario.
	private String nombre; 
	// Nombre de usuario único (dirección de correo electrónico).
	private String username; 
	// Contraseña encriptada para el acceso seguro al sistema.
	private String password;
	// Fecha de nacimiento del usuario cliente.
	private String fechaNacimiento; 
	// Atributo de tipo TipoUsuario para almacenar tipoUsuario.
	private TipoUsuario tipoUsuario; 
	// Peso corporal actual del cliente en kilogramos.
	private double peso; 
	// Altura del cliente en centímetros.
	private int altura; 
	// Nivel de actividad física diaria no asociada al ejercicio (NEAT).
	private int neat;
	// Código de vinculación único generado para el entrenador.
	private String trainerCode;
	// Nombre de archivo o URL del avatar del usuario.
	private String avatar;

	/**
	 * Constructor de la clase RegisterRequest con inyección de dependencias.
	 *
	 */
	public RegisterRequest() {}

	/**
	 * Recupera el valor actual de trainercode.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public String getTrainerCode() {
		return trainerCode;
	}

	/**
	 * Establece el valor de trainercode.
	 *
	 * @param trainerCode Parámetro de entrada para la operación.
	 */
	public void setTrainerCode(String trainerCode) {
		this.trainerCode = trainerCode;
	}
	
	/**
	 * Constructor de la clase RegisterRequest con inyección de dependencias.
	 *
	 * @param nombre Parámetro de entrada para la operación.
	 * @param username Parámetro de entrada para la operación.
	 * @param password Parámetro de entrada para la operación.
	 * @param tipoUsuario Parámetro de entrada para la operación.
	 * @param peso Parámetro de entrada para la operación.
	 * @param altura Parámetro de entrada para la operación.
	 * @param neat Parámetro de entrada para la operación.
	 */
	public RegisterRequest(String nombre, String username, String password, TipoUsuario tipoUsuario, double peso, int altura,
			int neat) {
		super();
		this.nombre = nombre;
		this.username = username;
		this.password = password;
		this.tipoUsuario = tipoUsuario;
		this.peso = peso;
		this.altura = altura;
		this.neat = neat;
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
	 * Recupera el valor actual de peso.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public double getPeso() {
		return peso;
	}

	/**
	 * Establece el valor de peso.
	 *
	 * @param peso Parámetro de entrada para la operación.
	 */
	public void setPeso(double peso) {
		this.peso = peso;
	}

	/**
	 * Recupera el valor actual de altura.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public int getAltura() {
		return altura;
	}

	/**
	 * Establece el valor de altura.
	 *
	 * @param altura Parámetro de entrada para la operación.
	 */
	public void setAltura(int altura) {
		this.altura = altura;
	}

	/**
	 * Recupera el valor actual de neat.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public int getNeat() {
		return neat;
	}

	/**
	 * Establece el valor de neat.
	 *
	 * @param neat Parámetro de entrada para la operación.
	 */
	public void setNeat(int neat) {
		this.neat = neat;
	}

	/**
	 * Recupera el valor actual de fechanacimiento.
	 *
	 * @return El resultado o estado devuelto tras procesar la petición.
	 */
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	/**
	 * Establece el valor de fechanacimiento.
	 *
	 * @param fechaNacimiento Parámetro de entrada para la operación.
	 */
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
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
